package com.dudev.orderservice.controller;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.model.enums.Status;
import com.dudev.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void create(@RequestBody CreateOrderDto createOrderDto,
                       @Parameter(hidden = true) @CurrentSecurityContext SecurityContext securityContext) {
        orderService.create(createOrderDto, securityContext);
    }

    @GetMapping
    public List<OrderDto> findCurrentUserOrders() {
        return orderService.findOrdersByCurrentUser();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDto> findAll() {
        return orderService.findAll();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id,
                                       @RequestBody Status status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
