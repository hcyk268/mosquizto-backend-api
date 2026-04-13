package com.mosquizto.api.security;

import com.mosquizto.api.model.RedisToken;
import com.mosquizto.api.service.RedisTokenService;
import com.mosquizto.api.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.mosquizto.api.util.TokenType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.resetKey}")
    private String resetKey;

    @Value("${jwt.expiryHour}")
    private int expiryHour;

    @Value("${jwt.expiryDay}")
    private int expiryDay;

    private final RedisTokenService redisTokenService;

    public String generateAccessToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * expiryHour))
                .signWith(getKey(ACCESS_TOKEN))
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * expiryDay * 24))
                .signWith(getKey(REFRESH_TOKEN))
                .compact();
    }

    public String generateResetToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 30 * 60))
                .signWith(getKey(RESET_TOKEN))
                .compact();
    }

    public String extractUsername(String token, TokenType type) {
        return extractClaims(token, type).getSubject();
    }

    public boolean isValid(String token, TokenType type, UserDetails user) {
        Claims claims = extractClaims(token, type);
        boolean base = claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date());
        if (type == RESET_TOKEN)
            return base;

        if (!base) return false;

        try {
            RedisToken storedToken = this.redisTokenService.getById(claims.getSubject());
            return switch (type) {
                case ACCESS_TOKEN -> token.equals(storedToken.getAccessToken());
                case REFRESH_TOKEN -> token.equals(storedToken.getRefreshToken());
                default -> false;
            };
        } catch (Exception e) {
            log.warn("Redis unavailable during token validation for user [{}]: {}", claims.getSubject(), e.getMessage());
            return false;
        }
    }

    private SecretKey getKey(TokenType type) {
        return switch (type) {
            case REFRESH_TOKEN -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            case ACCESS_TOKEN -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            case RESET_TOKEN -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
        };
    }

    private Claims extractClaims(String token, TokenType type) {
        return Jwts.parser()
                .verifyWith(getKey(type))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
