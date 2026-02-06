package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.exception.RefreshTokenNotFoundException;
import com.dudev.orderservice.exception.UserNotFoundException;
import com.dudev.orderservice.model.RefreshToken;
import com.dudev.orderservice.model.User;
import com.dudev.orderservice.repository.RefreshTokenRepository;
import com.dudev.orderservice.repository.UserRepository;
import com.dudev.orderservice.util.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements com.dudev.orderservice.service.JwtService {

    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, tokenProperties.getJwt().getExpiration());
    }

    private String generateToken(UserDetails userDetails, Integer expiration) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails, expiration);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Integer expiration) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenProperties.getJwt().getSalt());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, tokenProperties.getRefresh().getExpiration());
    }

    @Override
    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(RefreshTokenNotFoundException::new);
    }

    @Transactional
    @Override
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        RefreshToken token = RefreshToken.builder()
                .expiryDate(Instant.now().plus(Duration.of(tokenProperties.getRefresh().getExpiration(), ChronoUnit.MILLIS)))
                .user(user)
                .token(generateRefreshToken(user))
                .build();
        return refreshTokenRepository.save(token);
    }

    @Transactional
    @Override
    public RefreshToken createOrUpdateRefreshToken(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return refreshTokenRepository.findByUserId(user.getId())
                .map(existing -> updateRefreshToken(userId))
                .orElseGet(() -> createRefreshToken(userId));
    }

    @Transactional
    @Override
    public RefreshToken updateRefreshToken(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(it -> {
                    refreshTokenRepository.delete(it);
                    refreshTokenRepository.flush();
                    return RefreshToken.builder()
                            .user(it.getUser())
                            .token(generateRefreshToken(userRepository.findById(userId).orElseThrow()))
                            .expiryDate(Instant.now().plus(Duration.of(tokenProperties.getRefresh().getExpiration(), ChronoUnit.MILLIS)))
                            .build();
                })
                .orElseThrow(RefreshTokenNotFoundException::new);
        return refreshTokenRepository.save(refreshToken);
    }
}