package com.mosquizto.api.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.mosquizto.api.dto.request.GoogleLoginRequest;
import com.mosquizto.api.dto.request.ResetPasswordRequest;
import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.request.VerifyCodeRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.security.GoogleVerifier;
import com.mosquizto.api.service.AuthenticationService;
import com.mosquizto.api.util.AuthorizationHeaderUtils;
import com.mosquizto.api.util.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Register, login, token and password APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final GoogleVerifier googleVerifier;

    @Operation(summary = "Register account", description = "Create account and send confirmation email.", security = {})
    @ApiResponse(responseCode = "200", description = "Registered")
    @RateLimit(action = "register", maxRequests = 5, timeWindow = 600)
    @PostMapping("/register")
    public ResponseData<String> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "signup successfully, please check email and confirm", this.authenticationService.createAccount(signUpRequest));
    }

    @Operation(summary = "Login", description = "Return access token and refresh token.", security = {})
    @ApiResponse(responseCode = "200", description = "Logged in")
    @RateLimit(action = "login", maxRequests = 10, timeWindow = 60)
    @PostMapping("/login")
    public ResponseData<TokenResponse> login(
            @Valid @RequestBody SignInRequest signInRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login successfully", this.authenticationService.authenticate(signInRequest));
    }

    @Operation(summary = "Refresh token", description = "Use refresh token from Authorization header.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Token refreshed")
    @PostMapping("/refresh-token")
    public ResponseData<TokenResponse> refresh(HttpServletRequest request) {
        String refreshToken = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Token refreshed successfully", this.authenticationService.refreshToken(refreshToken));
    }

    @Operation(summary = "Logout", description = "Invalidate current user tokens.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Logged out")
    @PostMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest request) {
        String accessToken = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Logout successfully", this.authenticationService.logout(accessToken));
    }

    @Operation(summary = "Forgot password", description = "Send OTP to email.", security = {})
    @ApiResponse(responseCode = "200", description = "OTP sent")
    @RateLimit(action = "forgot-password", maxRequests = 5, timeWindow = 900)
    @PostMapping("/forgot-password")
    public ResponseData<?> forgotPassword(
            @RequestBody String email) {
        this.authenticationService.forgotPassword(email);
        return new ResponseData<>(HttpStatus.OK.value(), "Success. Sent verify code to your email");
    }

    @Operation(summary = "Verify reset OTP", description = "Validate OTP and return reset token.", security = {})
    @ApiResponse(responseCode = "200", description = "OTP verified")
    @RateLimit(action = "verify-code-forgot-password", maxRequests = 5, timeWindow = 900)
    @PostMapping("/verify-code-forgot-password")
    public ResponseData<ResetPasswordTokenResponse> verifyCodeForgotPassword(
            @Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.authenticationService.verifyCodeForgotPassword(verifyCodeRequest));
    }

    @Operation(summary = "Reset password", description = "Set new password using reset token.", security = {})
    @ApiResponse(responseCode = "200", description = "Password reset")
    @RateLimit(action = "reset-password", maxRequests = 5, timeWindow = 900)
    @PostMapping("/reset-password")
    public ResponseData<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        this.authenticationService.resetPassword(resetPasswordRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Success");
    }

    @Operation(summary = "Google login", description = "Login with Google ID token.", security = {})
    @ApiResponse(responseCode = "200", description = "Logged in")
    @RateLimit(action = "google-login", maxRequests = 5, timeWindow = 60)
    @PostMapping("/google")
    public ResponseData<TokenResponse> loginGoogle(@Valid @RequestBody GoogleLoginRequest googleLoginRequest) throws Exception {
        GoogleIdToken.Payload payload = this.googleVerifier.verify(googleLoginRequest.getIdToken());
        return new ResponseData<>(HttpStatus.OK.value(), "Login successfully", this.authenticationService.loginGoogle(payload));
    }
}
