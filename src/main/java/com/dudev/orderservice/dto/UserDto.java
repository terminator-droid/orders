package com.dudev.orderservice.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username
) {}
