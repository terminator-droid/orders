package com.dudev.orderservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshAccessDto {

    String refreshToken;
}
