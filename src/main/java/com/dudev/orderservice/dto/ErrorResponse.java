package com.dudev.orderservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String message, String code, LocalDateTime timestamp) {
}
