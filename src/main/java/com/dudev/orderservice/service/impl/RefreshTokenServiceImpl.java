package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.exception.RefreshTokenNotFoundException;
import com.dudev.orderservice.exception.UserNotFoundException;
import com.dudev.orderservice.model.RefreshToken;
import com.dudev.orderservice.model.User;
import com.dudev.orderservice.repository.RefreshTokenRepository;
import com.dudev.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements com.dudev.orderservice.service.RefreshTokenService {

    @Value("${token.refresh.expiration}")
    private Long refreshTokenExpiration;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(RefreshTokenNotFoundException::new);
    }

    @Override
    public RefreshToken create(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        RefreshToken token = RefreshToken.builder()
                .expiryDate(Instant.now().plus(Duration.of(refreshTokenExpiration, ChronoUnit.MILLIS)))
                .user(user)
                .token(generateRefreshToken())
                .build();
        return refreshTokenRepository.save(token);
    }

    @Transactional
    @Override
    public RefreshToken createOrUpdate(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return refreshTokenRepository.findByUserId(user.getId())
                .map(existing -> update(userId))
                .orElseGet(() -> create(userId));
    }

    @Transactional
    @Override
    public RefreshToken update(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(it -> {
                    refreshTokenRepository.delete(it);
                    refreshTokenRepository.flush();
                    return RefreshToken.builder()
                            .user(it.getUser())
                            .token(generateRefreshToken())
                            .expiryDate(Instant.now().plus(Duration.of(refreshTokenExpiration, ChronoUnit.MILLIS)))
                            .build();
                })
                .orElseThrow(RefreshTokenNotFoundException::new);
        return refreshTokenRepository.save(refreshToken);
    }
}
