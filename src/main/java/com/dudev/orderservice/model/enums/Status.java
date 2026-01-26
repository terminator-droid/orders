package com.dudev.orderservice.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Status {
    CREATED, IN_PROGRESS, COMPLETED;
}
