package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.dto.CreateUserDto;
import com.dudev.orderservice.dto.JwtAuthenticationResponse;
import com.dudev.orderservice.dto.RefreshAccessDto;
import com.dudev.orderservice.dto.SignInRequest;
import com.dudev.orderservice.dto.SignUpRequest;
import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.exception.RefreshTokenExpiredException;
import com.dudev.orderservice.model.RefreshToken;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements com.dudev.orderservice.service.AuthenticationService {

    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public JwtAuthenticationResponse refreshAccess(RefreshAccessDto refreshAccessDto) {
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        RefreshToken currentRefreshToken = refreshTokenService.findByToken(refreshAccessDto.getRefreshToken());
        if (currentRefreshToken.isExpired()) {
            throw new RefreshTokenExpiredException();
        }
        jwtAuthenticationResponse.setAccessToken(
                jwtService.generateToken(currentRefreshToken.getUser()));
        RefreshToken newRefreshToken = refreshTokenService.update(currentRefreshToken.getUser().getId());
        jwtAuthenticationResponse.setRefreshToken(newRefreshToken.getToken());
        return jwtAuthenticationResponse;
    }

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        CreateUserDto userDto = CreateUserDto.builder()
                .password(signUpRequest.getPassword())
                .username(signUpRequest.getUsername())
                .build();

        UserDto user = userService.createUser(userDto, Role.USER);

        return JwtAuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(userService.loadUserByUsername(userDto.getUsername())))
                .refreshToken(refreshTokenService.createOrUpdate(user.getId()).getToken())
                .build();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(), signInRequest.getPassword()));

        UserDetails userDetails = userService.loadUserByUsername(signInRequest.getUsername());
        return new JwtAuthenticationResponse(jwtService.generateToken(userDetails),
                refreshTokenService.createOrUpdate(userService.findByUsername(signInRequest.getUsername()).getId()).getToken());
    }
}
