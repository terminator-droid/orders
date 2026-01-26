package com.dudev.orderservice.service;

import com.dudev.orderservice.model.RefreshToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface RefreshTokenService {
    String generateRefreshToken();

    RefreshToken findByToken(String token);

    @Transactional
    RefreshToken createOrUpdate(UUID userId);

    @Transactional
    RefreshToken update(UUID userId);

    RefreshToken create(UUID userID);
}
