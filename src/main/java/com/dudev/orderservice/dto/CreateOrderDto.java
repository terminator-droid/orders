package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateOrderDto {

    @NotBlank
    String description;
}
