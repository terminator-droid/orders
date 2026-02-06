package com.dudev.orderservice.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID userId) {
        super("Order not found with id=" + userId);
    }

}
