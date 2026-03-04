package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signIndata);

    TokenResponse refreshToken(HttpServletRequest request);
}
