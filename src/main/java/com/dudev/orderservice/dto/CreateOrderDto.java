package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderDto {

    @NotBlank
    private final String description;
}
