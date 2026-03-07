package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signIndata);

    TokenResponse refreshToken(HttpServletRequest request);

    String createAccount(@Valid SignUpRequest signUpRequest);
}
