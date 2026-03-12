package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.ResetPasswordRequest;
import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.request.VerifyCodeRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseData<String> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "signup successfully, please check email and confirm", this.authenticationService.createAccount(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseData<TokenResponse> login(@Valid @RequestBody SignInRequest signInRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login successfully", this.authenticationService.authenticate(signInRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseData<TokenResponse> refresh(HttpServletRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Token refreshed successfully", this.authenticationService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Logout successfully", this.authenticationService.logout(request));
    }

    @PostMapping("/forgot-password")
    public ResponseData<?> forgotPassword(@RequestBody String email) {
        this.authenticationService.forgotPassword(email);
        return new ResponseData<>(HttpStatus.OK.value(), "Success. Sent verify code to your email");
    }

    @PostMapping("/reset-password")
    public ResponseData<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        this.authenticationService.resetPassword(resetPasswordRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Success");
    }

    @PostMapping("/verify-code-forgot-password")
    public ResponseData<ResetPasswordTokenResponse> verifyCodeForgotPassword(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.authenticationService.verifyCodeForgotPassword(verifyCodeRequest));
    }
}
