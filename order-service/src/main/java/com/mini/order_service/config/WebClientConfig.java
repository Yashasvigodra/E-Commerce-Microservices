//package com.mini.order_service.config;
//
//
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    @LoadBalanced
//    public WebClient.Builder webClientBuilder(){
//        return WebClient.builder();
//    }//this will create a WebClient bean that can be injected into
//    // other components of the application, allowing you to make HTTP
//    // requests to other services (like the inventory service) in a reactive
//    // and non-blocking way.
//
//}
