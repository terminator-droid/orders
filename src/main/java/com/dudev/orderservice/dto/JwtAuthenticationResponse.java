package com.dudev.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Ответ c токеном доступа")
public class JwtAuthenticationResponse {

    @Schema(description = "Токен доступа")
    private final String accessToken;
    private final String refreshToken;
}