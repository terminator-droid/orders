package com.dudev.orderservice.service;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.model.enums.Status;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto create(CreateOrderDto createOrderDto, SecurityContext securityContext);

    List<OrderDto> findByUser(UUID userId);

    List<OrderDto> findAll();

    void updateOrderStatus(UUID orderId, Status status);

    void deleteOrder(UUID orderId);

    List<OrderDto> findOrdersByCurrentUser();
}