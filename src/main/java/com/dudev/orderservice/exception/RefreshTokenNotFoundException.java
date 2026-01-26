package com.dudev.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException() {
        super("Refresh token not found");
    }
}
