package com.dudev.orderservice.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class FullUserDto extends UserDto {

    private final String role;
}