package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.exception.OrderNotFoundException;
import com.dudev.orderservice.mapper.OrderMapper;
import com.dudev.orderservice.model.Order;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.model.enums.Status;
import com.dudev.orderservice.repository.OrderRepository;
import com.dudev.orderservice.repository.UserRepository;
import com.dudev.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements com.dudev.orderservice.service.OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public OrderDto create(CreateOrderDto createOrderDto, SecurityContext securityContext) {
        return Optional.of(createOrderDto)
                .map(orderMapper::toEntity)
                .map(order -> {
                    UserDto userDto = userService.findByUsername(((UserDetails) securityContext.getAuthentication().getPrincipal())
                            .getUsername());
                    order.setUser(userRepository.getUserByUsername(userDto.getUsername()).orElseThrow());
                    order.setStatus(Status.CREATED);
                    return order;
                })
                .map(orderRepository::save)
                .map(orderMapper::toDto)
                .orElseThrow();
    }

    @Override
    public List<OrderDto> findByUser(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream().map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId, Status status) {
        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    return order;
                })
                .map(orderRepository::saveAndFlush)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional
    @Override
    public void deleteOrder(UUID orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        String currentUsername = ((User) authentication.getPrincipal()).getUsername();

        if (!order.getUser().getUsername().equals(currentUsername)
                && authentication.getAuthorities().stream()
                .noneMatch(role -> role.equals(Role.ADMIN))) {
            throw new AccessDeniedException("Not owner or admin");
        }
            orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderDto> findOrdersByCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserDto userDto = userService.findByUsername(username);
        return orderRepository.findByUserId(userDto.getId())
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }
}