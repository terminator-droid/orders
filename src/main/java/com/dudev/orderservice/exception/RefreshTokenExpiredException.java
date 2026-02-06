package com.dudev.orderservice.exception;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException() {
        super("Refresh token has expired, please log in");
    }
}
