package com.mosquizto.api.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ResetPasswordRequest;
import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.request.VerifyCodeRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.InvalidTokenException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.AuthenticationMapper;
import com.mosquizto.api.model.RedisToken;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.security.JwtService;
import com.mosquizto.api.service.AuthenticationService;
import com.mosquizto.api.service.MailService;
import com.mosquizto.api.service.RedisTokenService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.TokenType;
import com.mosquizto.api.util.UserStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String OTP_KEY_PREFIX = "password-reset-otp:";
    private static final String OTP_FIELD = "code";
    private static final long OTP_TTL_MINUTES = 15;
    private static final String DEFAULT_ROLE = "USER";
    private static final String GOOGLE_USERNAME_FALLBACK = "googleuser";
    private static final SecureRandom random = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationMapper authenticationMapper;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiryDay}")
    private int expiryDay;

    @Override
    public TokenResponse authenticate(SignInRequest signIndata) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signIndata.getUsername(), signIndata.getPassword()));

        var user = this.userService.getByUsername(signIndata.getUsername());

        String accessToken = this.jwtService.generateAccessToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(user);

        long ttlSeconds = (long) expiryDay * 24 * 60 * 60;
        RedisToken redisToken = RedisToken.initiate(user.getUsername(), accessToken, refreshToken, ttlSeconds);
        this.redisTokenService.save(redisToken);

        return this.authenticationMapper.toTokenResponse(user, accessToken, refreshToken);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        String username;
        try {
            username = this.jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
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

        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidTokenException("Not allow access with this token");
        }

        String accessToken = this.jwtService.generateAccessToken(user);

        long ttlSeconds = (long) expiryDay * 24 * 60 * 60;
        RedisToken redisToken = RedisToken.initiate(user.getUsername(), accessToken, refreshToken, ttlSeconds);
        this.redisTokenService.save(redisToken);

        return this.authenticationMapper.toTokenResponse(user, accessToken, refreshToken);
    }

    @Override
    public String createAccount(SignUpRequest signUpRequest) {
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword()))
            throw new InvalidDataException("Password not match");

        AddUserRequest user = this.authenticationMapper.toAddUserRequest(signUpRequest);

        long userId = this.userService.addUser(user);

        String verifyCode = UUID.randomUUID().toString();
        this.userService.saveVerifyCode(userId, verifyCode);

        this.mailService.sendConfirmLink(signUpRequest.getEmail(), userId, signUpRequest.getFullName(), verifyCode);

        return signUpRequest.getUsername();
    }

    @Override
    public String logout(String accessToken) {
        String username;
        try {
            username = this.jwtService.extractUsername(accessToken, TokenType.ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }

        this.redisTokenService.deleteById(username);

        return username;
    }

    @Override
    public void forgotPassword(String email) {
        var user = this.userService.getByEmail(email);

        String verCode = this.generateVerifyCode(8);

        String otpKey = this.passwordResetOtpKey(user.getId());
        this.redisTemplate.opsForHash().put(otpKey, OTP_FIELD, verCode);
        this.redisTemplate.expire(otpKey, OTP_TTL_MINUTES, TimeUnit.MINUTES);

        this.mailService.sendVerifyCode(email, verCode);
    }

    @Override
    public ResetPasswordTokenResponse verifyCodeForgotPassword(VerifyCodeRequest verifyCodeRequest) {
        var user = this.userService.getByEmail(verifyCodeRequest.getEmail());

        String otpKey = this.passwordResetOtpKey(user.getId());
        Object otp = this.redisTemplate.opsForHash().get(otpKey, OTP_FIELD);
        if (otp == null || !otp.toString().equals(verifyCodeRequest.getCode())) {
            throw new InvalidDataException("Verify Code Invalid");
        }

        Long deletedOtp = this.redisTemplate.opsForHash().delete(otpKey, OTP_FIELD);
        if (deletedOtp == null || deletedOtp == 0) {
            throw new InvalidDataException("Verify Code Invalid");
        }

        String resetToken = this.jwtService.generateResetToken(user);

        return this.authenticationMapper.toResetPasswordTokenResponse(verifyCodeRequest.getEmail(), resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword()))
            throw new InvalidDataException("Password not match");

        String username = this.jwtService.extractUsername(resetPasswordRequest.getSecretKey(), TokenType.RESET_TOKEN);
        var user = this.userService.getByUsername(username);

        if (!this.jwtService.consumeResetToken(resetPasswordRequest.getSecretKey(), user))
            throw new InvalidTokenException("Token invalid");

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        this.userService.save(user);
    }

    @Override
    public TokenResponse loginGoogle(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        if (email == null || email.isBlank() || !Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new InvalidDataException("Google account is invalid");
        }

        User user = this.getOrCreateGoogleUser(payload);

        String accessToken = this.jwtService.generateAccessToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(user);
        long ttlSeconds = (long) expiryDay * 24 * 60 * 60;
        RedisToken redisToken = RedisToken.initiate(user.getUsername(), accessToken, refreshToken, ttlSeconds);
        this.redisTokenService.save(redisToken);

        return this.authenticationMapper.toTokenResponse(user, accessToken, refreshToken);
    }

    private String generateVerifyCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    private String passwordResetOtpKey(Long userId) {
        return OTP_KEY_PREFIX + userId;
    }

    private User getOrCreateGoogleUser(GoogleIdToken.Payload payload) {
        User user = this.userService.checkEmailExists(payload.getEmail())
                ? this.userService.getByEmail(payload.getEmail())
                : User.builder()
                .email(payload.getEmail())
                .username(this.generateGoogleUsername(payload.getEmail()))
                .password(this.passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();

        if (user.getFullName() == null || user.getFullName().isBlank()) {
            user.setFullName(this.getGoogleName(payload, payload.getEmail()));
        }

        if (user.getRole() == null) {
            user.setRole(this.getDefaultUserRole());
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setVerifyCode(null);
        this.userService.save(user);
        return user;
    }

    private Role getDefaultUserRole() {
        return this.roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + DEFAULT_ROLE));
    }

    private String getGoogleName(GoogleIdToken.Payload payload, String email) {
        Object name = payload.get("name");
        if (name instanceof String value && !value.isBlank()) {
            return value;
        }

        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private String generateGoogleUsername(String email) {
        int atIndex = email.indexOf("@");
        String base = (atIndex > 0 ? email.substring(0, atIndex) : email)
                .replaceAll("[^A-Za-z0-9._-]", "");
        if (base.isBlank()) {
            base = GOOGLE_USERNAME_FALLBACK;
        }
        if (base.length() > 95) {
            base = base.substring(0, 95);
        }

        String username = base;
        while (this.userService.checkUsernameExists(username)) {
            username = base + (10000 + random.nextInt(90000));
        }
        return username;
    }
}
