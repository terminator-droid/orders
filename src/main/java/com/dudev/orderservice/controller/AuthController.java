package com.dudev.orderservice.controller;

import com.dudev.orderservice.dto.JwtAuthenticationResponse;
import com.dudev.orderservice.dto.RefreshAccessDto;
import com.dudev.orderservice.dto.SignInRequest;
import com.dudev.orderservice.dto.SignUpRequest;
import com.dudev.orderservice.dto.UserDtoFull;
import com.dudev.orderservice.service.AuthenticationService;
import com.dudev.orderservice.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationServiceImpl;
    private final UserServiceImpl userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return authenticationServiceImpl.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest signInRequest) {
        return authenticationServiceImpl.signIn(signInRequest);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationResponse refreshAccess(@RequestBody RefreshAccessDto refreshAccessDto) {
        return authenticationServiceImpl.refreshAccess(refreshAccessDto);
    }

    @GetMapping("/me")
    @PostAuthorize("isAuthenticated()")
    public UserDtoFull  getCurrentUserDetails() {
        return userService.getCurrentUser();
    }
}
