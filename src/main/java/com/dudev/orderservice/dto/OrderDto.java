package com.dudev.orderservice.dto;

import java.util.UUID;

public record OrderDto(
        UUID id,
        UserDto user,
        String description,
        String status
) {
}
