package com.mini.order_service.exceptions;

public class CustomerNotFoundException extends  RuntimeException {

    public CustomerNotFoundException(String message){
        super(message);
    }
}
