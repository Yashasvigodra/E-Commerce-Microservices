package com.mini.order_service.service;


import com.mini.order_service.client.CustomerClient;
import com.mini.order_service.client.InventoryClient;
import com.mini.order_service.dto.*;
import com.mini.order_service.event.OrderPlacedEvent;
import com.mini.order_service.exceptions.CustomerNotFoundException;
import com.mini.order_service.exceptions.InvalidOrderStateException;
import com.mini.order_service.exceptions.OrderNotFoundException;
import com.mini.order_service.model.Order;
import com.mini.order_service.model.OrderLineItem;
import com.mini.order_service.model.OrderStatus;
import com.mini.order_service.repository.OrderRepository;
import feign.FeignException;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;
    private final CustomerClient customerClient;
    private final Tracer tracer;

    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;



    public String placeOrder(OrderRequest orderRequest) {
        CustomerResponse customer = validateAndFetchCustomer(orderRequest.getCustomerId());
        Order order = buildOrder(orderRequest);
        validateStock(order.getOrderLineItemsList());     // checks sufficient qty
        reduceInventory(order.getOrderLineItemsList());   // ✅ actually deducts stock


        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        kafkaTemplate.send("notificationTopic",
                OrderPlacedEvent.builder()
                        .orderNumber(order.getOrderNumber())
                        .customerId(order.getCustomerId())
                        .customerEmail(customer.getEmail())           // ← from step 1
                        .customerName(customer.getFirstName())  // ← attach customer
                        .build()
        );

        return "Order Placed Successfully";
    }

    // ──────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────


    // fetches full customer, returns it for reuse
    private CustomerResponse validateAndFetchCustomer(String customerId) {
        try {
            return customerClient.findById(customerId);   // returns full customer object
        } catch (FeignException.NotFound e) {
            throw new CustomerNotFoundException(
                    "Cannot place order: No customer found with ID: " + customerId
            );
        }
    }

        private Order buildOrder(OrderRequest request) {
        List<OrderLineItem> lineItems = request.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToOrderLineItem)
                .toList();

        BigDecimal totalPrice = calculateTotalPrice(lineItems);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderLineItemsList(lineItems);
        order.setTotalPrice(totalPrice);
        return order;
    }

    private BigDecimal calculateTotalPrice(List<OrderLineItem> lineItems) {
        return lineItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateStock(List<OrderLineItem> lineItems) {
        List<InventoryCheckRequest> checkRequests = lineItems.stream()
                .map(item -> InventoryCheckRequest.builder()
                        .skuCode(item.getSkuCode())
                        .requiredQuantity(item.getQuantity())  //  passes actual needed qty
                        .build())
                .toList();

        List<InventoryResponse> responses = inventoryClient.checkStock(checkRequests);

        Map<String, InventoryResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(InventoryResponse::getSkuCode, r -> r));

        List<String> notFound = lineItems.stream()
                .map(item -> item.getSkuCode())
                .filter(sku -> !responseMap.containsKey(sku))
                .toList();

        List<String> outOfStock = responses.stream()
                .filter(r -> !r.getIsInStock())
                .map(InventoryResponse::getSkuCode)
                .toList();

        if (!notFound.isEmpty() || !outOfStock.isEmpty()) {
            throw new InvalidOrderStateException("Order validation failed", notFound, outOfStock);
        }
    }

    private void reduceInventory(List<OrderLineItem> lineItems) {
        lineItems.forEach(item ->
                inventoryClient.reduceStock(item.getSkuCode(), item.getQuantity())
        );
    }











    // PURPOSE: Retrieves all orders from the database and converts them into response DTOs
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PURPOSE: Retrieves a single order by its orderNumber
    public OrderResponse getOrder(String orderNumber) {

        Order order = findOrder(orderNumber);

        return mapToResponse(order);
    }


    // PURPOSE: Cancels an order if it has not already been shipped or delivered
    public void cancelOrder(String orderNumber) {

        Order order = findOrder(orderNumber);

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStateException(
                    "Cannot cancel shipped or delivered orders"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    // PURPOSE: Updates the status of an order (e.g., CONFIRMED, SHIPPED, DELIVERED)
    public void updateStatus(String orderNumber, OrderStatus status) {

        Order order = findOrder(orderNumber);

        order.setStatus(status);
    }

    // PURPOSE: Helper method to fetch an order by orderNumber from the database
    // Throws exception if the order does not exist
    private Order findOrder(String orderNumber) {

        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with number: " + orderNumber)
                );
    }

    // PURPOSE: Converts Order entity into OrderResponse DTO for API responses
    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }
    private OrderLineItem mapToOrderLineItem(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItem;
    }

}
