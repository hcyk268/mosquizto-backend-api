package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.ResetPasswordRequest;
import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.request.VerifyCodeRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signIndata);

    TokenResponse refreshToken(String refreshToken);

    String createAccount(SignUpRequest signUpRequest);

    String logout(String accessToken);

    void forgotPassword(String email);

    ResetPasswordTokenResponse verifyCodeForgotPassword(VerifyCodeRequest verifyCodeRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
