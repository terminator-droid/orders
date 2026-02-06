package com.dudev.orderservice.service;

import com.dudev.orderservice.dto.JwtAuthenticationResponse;
import com.dudev.orderservice.dto.RefreshAccessDto;
import com.dudev.orderservice.dto.SignInRequest;
import com.dudev.orderservice.dto.SignUpRequest;

public interface AuthenticationService {
    JwtAuthenticationResponse refreshAccess(RefreshAccessDto refreshAccessDto);

    JwtAuthenticationResponse signUp(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signIn(SignInRequest signInRequest);
}
