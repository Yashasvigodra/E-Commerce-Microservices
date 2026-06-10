package com.mini.s.product.service;

import com.mini.s.product.service.dto.ProductRequest;
import com.mini.s.product.service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container // so that junit 5 understands that this is a container ,it should be started before the tests and stopped after the tests
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2"); //docker image for mongodb

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository; // to interact with the database and verify that the product is created in the database
    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }
//    @Autowired
//    private ObjectMapper objectMapper; // to convert the product request object to json string


    @DynamicPropertySource // to set the properties for the tests, in this case we want to set the mongodb uri to the one provided by the container dynamically, so that we can connect to the mongodb instance running in the container
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl); // set the mongodb uri to the one provided by the container
    }

    @Test
	void shouldCreateProduct() throws Exception {

//        ProductRequest productRequest  = getProductRequest();
//        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                        {
                            "name": "iPhone 14",
                            "description": "Latest iPhone model",
                            "price": 1200
                        }
                        """.stripIndent()
                ))
                .andExpect(status().isCreated());
        Assertions.assertEquals( 1, productRepository.findAll().size()); // verify that the product is created in the database


	}

//    private ProductRequest getProductRequest() {
//        return ProductRequest.builder()
//                .name("iPhone 14")
//                .description("Latest iPhone model")
//                .price(BigDecimal.valueOf(1200))
//                .build();
//    }


    @Test
    void shouldGetAllProducts() throws Exception{
        //first create pdt
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                        {
                            "name": "iPhone 14",
                            "description": "Latest iPhone model",
                            "price": 1200
                        }
                        """.stripIndent()
                ))
                .andExpect(status().isCreated());

        //then get all pdts
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
