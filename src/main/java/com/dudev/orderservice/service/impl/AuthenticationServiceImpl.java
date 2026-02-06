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
import com.dudev.orderservice.service.JwtService;
import com.dudev.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements com.dudev.orderservice.service.AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    

    @Override
    public JwtAuthenticationResponse refreshAccess(RefreshAccessDto refreshAccessDto) {
        RefreshToken currentRefreshToken = jwtService.findByRefreshToken(refreshAccessDto.getRefreshToken());
        if (currentRefreshToken.isExpired()) {
            throw new RefreshTokenExpiredException();
        }
        String accessToken = jwtService.generateAccessToken(currentRefreshToken.getUser());
        RefreshToken newRefreshToken = jwtService.updateRefreshToken(currentRefreshToken.getUser().getId());
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken).refreshToken(newRefreshToken.getToken()).build();
    }

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        CreateUserDto userDto = CreateUserDto.builder()
                .password(signUpRequest.getPassword())
                .username(signUpRequest.getUsername())
                .build();

        UserDto user = userService.createUser(userDto, Role.USER);

        return JwtAuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(userService.loadUserByUsername(userDto.getUsername())))
                .refreshToken(jwtService.createOrUpdateRefreshToken(user.getId()).getToken())
                .build();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(), signInRequest.getPassword()));

        UserDetails userDetails = userService.loadUserByUsername(signInRequest.getUsername());
        return JwtAuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetails))
                .refreshToken(jwtService.createOrUpdateRefreshToken(userService.findByUsername(signInRequest.getUsername()).getId()).getToken())
                .build();
    }
}