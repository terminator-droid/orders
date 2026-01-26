package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserDto {

    @NotBlank
    @Size(min = 5, max = 20)
    String username;
    @NotBlank
    @Size(min = 8, max = 255)
    String password;
}
