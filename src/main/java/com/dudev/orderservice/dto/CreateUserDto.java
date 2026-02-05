package com.dudev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDto {

    @NotBlank
    @Size(min = 5, max = 20)
    private final String username;
    @NotBlank
    @Size(min = 8, max = 255)
    private final String password;
}
