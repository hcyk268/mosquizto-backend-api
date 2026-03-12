package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.*;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.InvalidTokenException;
import com.mosquizto.api.service.*;
import com.mosquizto.api.util.TokenType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public TokenResponse authenticate(SignInRequest signIndata) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signIndata.getUsername(), signIndata.getPassword()));

        var user = this.userService.getByUsername(signIndata.getUsername());

        String accessToken = this.jwtService.generateAccessToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(user);

        this.tokenService.save(user.getUsername(), accessToken, refreshToken);

        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is required");
        }

        String refresh = authorization.substring("Bearer ".length());

        String username;
        try {
            username = this.jwtService.extractUsername(refresh, TokenType.REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("Refresh token has expired: {}", e.getMessage());
            throw new InvalidTokenException("Refresh token has expired");
        } catch (MalformedJwtException e) {
            log.error("Invalid refresh token format: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token format");
        } catch (SignatureException e) {
            log.error("Invalid refresh token signature: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token signature");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Unsupported refresh token");
        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        }

        var user = this.userService.getByUsername(username);

        if (!jwtService.isValid(refresh, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidTokenException("Not allow access with this token");
        }

        String accessToken = this.jwtService.generateAccessToken(user);

        this.tokenService.save(user.getUsername(), accessToken, refresh);

        return TokenResponse.builder()
                .userId(user.getId())
                .refreshToken(refresh)
                .accessToken(accessToken)
                .build();
    }

    @Override
    public String createAccount(SignUpRequest signUpRequest) {
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword()))
            throw new InvalidDataException("Password not match");

        AddUserRequest user = AddUserRequest.builder()
                .fullName(signUpRequest.getFullName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .role("USER")
                .build();

        long userId = this.userService.addUser(user);

        String verifyCode = UUID.randomUUID().toString();
        this.userService.saveVerifyCode(userId, verifyCode);

        this.mailService.sendConfirmLink(signUpRequest.getEmail(), userId, signUpRequest.getFullName(), verifyCode);

        return signUpRequest.getUsername();
    }

    @Override
    public String logout(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is required");
        }

        String accessToken = authorization.substring("Bearer ".length());

        String username;
        try {
            username = this.jwtService.extractUsername(accessToken, TokenType.ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }

        this.tokenService.deleteByUsername(username);

        return username;
    }

    @Override
    public void forgotPassword(String email) {
        var user = this.userService.getByEmail(email);

        String verCode = this.generateVerifyCode(8);

        this.userService.saveVerifyCode(user.getId(), verCode);

        this.mailService.sendVerifyCode(email, verCode);
    }

    @Override
    public ResetPasswordTokenResponse verifyCodeForgotPassword(VerifyCodeRequest verifyCodeRequest) {
        var user = this.userService.getByEmail(verifyCodeRequest.getEmail());

        if (!user.getVerifyCode().equals(verifyCodeRequest.getCode())) {
            throw new InvalidDataException("Verify Code Invalid");
        }

        String resetToken = this.jwtService.generateResetToken(user);

        return ResetPasswordTokenResponse.builder()
                .secretKey(resetToken)
                .email(verifyCodeRequest.getEmail())
                .build();
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword()))
            throw new InvalidDataException("Password not match");

        String username = this.jwtService.extractUsername(resetPasswordRequest.getSecretKey(), TokenType.RESET_TOKEN);

        var user = this.userService.getByUsername(username);

        if (!this.jwtService.isValid(resetPasswordRequest.getSecretKey(), TokenType.RESET_TOKEN, user))
            throw new InvalidTokenException("Token invalid");

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        this.userService.save(user);

    }

    private String generateVerifyCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
