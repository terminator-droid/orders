package com.dudev.orderservice.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
public class UserDto {
    protected final UUID id;
    protected final String username;
}
