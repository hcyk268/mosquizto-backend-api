package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.ResetPasswordRequest;
import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.request.VerifyCodeRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication: register, login, token management and password reset")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // ─────────────────────────────────────────────────────────── REGISTER ──

    @Operation(
            summary = "Register a new account",
            description = """
                    Creates a new user account. Upon successful registration, the system sends a
                    **confirmation email** to the provided address.
                    The user must click the link in the email before being able to log in.
                    
                    **Password rules:** minimum 8 characters, must contain uppercase, lowercase and a digit.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "signup successfully, please check email and confirm",
                                      "data": "nguyenvana"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payload (passwords do not match, missing field, weak password...)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Password not match", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/auth/register",
                                              "error": "Invalid Payload",
                                              "message": "Password not match"
                                            }
                                            """),
                                    @ExampleObject(name = "Weak password", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/auth/register",
                                              "error": "Invalid Payload",
                                              "message": "password: Password must be at least 8 characters, contain uppercase, lowercase and digit"
                                            }
                                            """),
                                    @ExampleObject(name = "Missing field", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/auth/register",
                                              "error": "Invalid Payload",
                                              "message": "email must be not blank"
                                            }
                                            """)
                            })
            )
    })
    @PostMapping("/register")
    public ResponseData<String> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "signup successfully, please check email and confirm", this.authenticationService.createAccount(signUpRequest));
    }

    // ──────────────────────────────────────────────────────────────── LOGIN ──

    @Operation(
            summary = "Login",
            description = """
                    Authenticates the user and returns an **access token** (expires in 1 hour)
                    and a **refresh token** (expires in 14 days).
                    
                    > ⚠️ The account must have a verified email before login is allowed.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Login successfully",
                                      "data": {
                                        "user_id": 42,
                                        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIn0.abc123",
                                        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIn0.xyz789"
                                      }
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing username or password",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 400,
                                      "path": "/auth/login",
                                      "error": "Invalid Payload",
                                      "message": "username must be not blank"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/auth/login",
                                      "error": "Unauthorized",
                                      "message": "Bad credentials"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Account email not yet verified",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 403,
                                      "path": "/auth/login",
                                      "error": "Forbidden",
                                      "message": "Account is not activated. Please verify your email."
                                    }
                                    """))
            )
    })
    @PostMapping("/login")
    public ResponseData<TokenResponse> login(
            @Valid @RequestBody SignInRequest signInRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login successfully", this.authenticationService.authenticate(signInRequest));
    }

    // ─────────────────────────────────────────────────────── REFRESH TOKEN ──

    @Operation(
            summary = "Refresh Access Token",
            description = """
                    Use the **refresh token** in the `Authorization: Bearer <refresh_token>` header
                    to obtain a new **access token** without logging in again.
                    
                    The refresh token remains unchanged; only the access token is renewed.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Token refreshed successfully",
                                      "data": {
                                        "user_id": 42,
                                        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new_access_token.sig",
                                        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.same_refresh_token.sig"
                                      }
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is missing, invalid or expired",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Token required", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 401,
                                              "path": "/auth/refresh-token",
                                              "error": "Unauthorized",
                                              "message": "Token is required"
                                            }
                                            """),
                                    @ExampleObject(name = "Token expired", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 401,
                                              "path": "/auth/refresh-token",
                                              "error": "Unauthorized",
                                              "message": "Refresh token has expired"
                                            }
                                            """),
                                    @ExampleObject(name = "Invalid signature", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 401,
                                              "path": "/auth/refresh-token",
                                              "error": "Unauthorized",
                                              "message": "Invalid refresh token signature"
                                            }
                                            """)
                            })
            )
    })
    @PostMapping("/refresh-token")
    public ResponseData<TokenResponse> refresh(HttpServletRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Token refreshed successfully", this.authenticationService.refreshToken(request));
    }

    // ─────────────────────────────────────────────────────────────── LOGOUT ──

    @Operation(
            summary = "Logout",
            description = """
                    Invalidates all current tokens for the authenticated user.
                    Requires `Authorization: Bearer <access_token>` in the header.
                    An expired access token is still accepted for logout.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Logout successfully",
                                      "data": "johndoe"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No token provided in the Authorization header",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/auth/logout",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            )
    })
    @PostMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Logout successfully", this.authenticationService.logout(request));
    }

    // ───────────────────────────────────────────────────── FORGOT PASSWORD ──

    @Operation(
            summary = "Forgot Password",
            description = """
                    Sends an **8-character OTP** (uppercase letters + digits) to the user's email address.
                    
                    > **Note:** The request body is a **raw string** containing the email address,
                    > not a JSON object. Example: `"user@example.com"`
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP sent successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Success. Sent verify code to your email"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email does not exist in the system",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 404,
                                      "path": "/auth/forgot-password",
                                      "error": "Not Found",
                                      "message": "User not found with email: user@example.com"
                                    }
                                    """))
            )
    })
    @PostMapping("/forgot-password")
    public ResponseData<?> forgotPassword(
            @RequestBody String email) {
        this.authenticationService.forgotPassword(email);
        return new ResponseData<>(HttpStatus.OK.value(), "Success. Sent verify code to your email");
    }

    // ────────────────────────────────────────────── VERIFY CODE (FORGOT PW) ──

    @Operation(
            summary = "Verify OTP Code",
            description = """
                    Verifies the OTP received by email after calling `/auth/forgot-password`.
                    If valid, returns a **secretKey** (JWT reset token) to be used in the reset-password step.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP is valid, secretKey returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Success",
                                      "data": {
                                        "email": "johndoe@example.com",
                                        "secretKey": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.reset_payload.sig"
                                      }
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "OTP is incorrect or request payload is invalid",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Wrong OTP", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/auth/verify-code-forgot-password",
                                              "error": "Invalid Payload",
                                              "message": "Verify Code Invalid"
                                            }
                                            """),
                                    @ExampleObject(name = "Invalid email format", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/auth/verify-code-forgot-password",
                                              "error": "Invalid Payload",
                                              "message": "email: must be a well-formed email address"
                                            }
                                            """)
                            })
            )
    })
    @PostMapping("/verify-code-forgot-password")
    public ResponseData<ResetPasswordTokenResponse> verifyCodeForgotPassword(
            @Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.authenticationService.verifyCodeForgotPassword(verifyCodeRequest));
    }

    // ──────────────────────────────────────────────────────── RESET PASSWORD ──

    @Operation(
            summary = "Reset Password",
            description = """
                    Sets a new password using the `secretKey` received from the OTP verification step.
                    `newPassword` and `confirmPassword` must match and satisfy the password rules.
                    
                    **Password rules:** minimum 8 characters, must contain uppercase, lowercase and a digit.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Success"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Passwords do not match or are not strong enough",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 400,
                                      "path": "/auth/reset-password",
                                      "error": "Invalid Payload",
                                      "message": "Password not match"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "secretKey is invalid or has expired",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Invalid token", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 401,
                                              "path": "/auth/reset-password",
                                              "error": "Unauthorized",
                                              "message": "Token invalid"
                                            }
                                            """),
                                    @ExampleObject(name = "Token expired", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 401,
                                              "path": "/auth/reset-password",
                                              "error": "Unauthorized",
                                              "message": "Token has expired"
                                            }
                                            """)
                            })
            )
    })
    @PostMapping("/reset-password")
    public ResponseData<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        this.authenticationService.resetPassword(resetPasswordRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Success");
    }
}
