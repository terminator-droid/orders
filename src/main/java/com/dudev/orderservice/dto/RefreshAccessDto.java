package com.dudev.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshAccessDto {

    private final String refreshToken;
}
