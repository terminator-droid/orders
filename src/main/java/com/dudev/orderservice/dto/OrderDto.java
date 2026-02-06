package com.dudev.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private final UUID id;
    private final UserDto user;
    private final String description;
    private final String status;
}
