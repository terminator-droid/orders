package com.dudev.orderservice.dto;

import com.dudev.orderservice.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class StatusUpdateDto {

    @Schema(description = "Новый статус", example = "ACTIVE")
    Status status;
}
