package com.mini.order_service.dto;


import com.mini.order_service.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String orderNumber;
    private String customerId;
    private OrderStatus status;
    private BigDecimal totalPrice;

    //razorpay specific fields
    private String razorpayOrderId;   // ← client needs this to open payment UI
    private Long amountInPaise;

}
