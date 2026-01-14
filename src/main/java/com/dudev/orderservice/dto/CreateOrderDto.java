package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDto(
        @NotBlank
        String description
) {
}
