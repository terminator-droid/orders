package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank
        @Size(min = 5, max = 20)
        String username,
        @NotBlank
        @Size(min = 8)
        String password
) {
}
