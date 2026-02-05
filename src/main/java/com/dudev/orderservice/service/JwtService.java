package com.dudev.orderservice.service;

import com.dudev.orderservice.model.RefreshToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {

    String extractUserName(String token);

    String generateAccessToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

    RefreshToken findByRefreshToken(String token);

    RefreshToken createRefreshToken(UUID userId);

    RefreshToken createOrUpdateRefreshToken(UUID userId);

    RefreshToken updateRefreshToken(UUID userId);
}