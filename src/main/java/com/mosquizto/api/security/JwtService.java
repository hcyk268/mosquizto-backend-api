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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mosquizto.api.util.TokenType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private static final String RESET_TOKEN_KEY_PREFIX = "password-reset-token:";
    private static final long RESET_TOKEN_TTL_MINUTES = 30;

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
    private final RedisTemplate<String, Object> redisTemplate;

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
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .id(tokenId)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 30 * 60))
                .signWith(getKey(RESET_TOKEN))
                .compact();
        this.redisTemplate.opsForValue().set(resetTokenKey(tokenId), user.getUsername(), RESET_TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    public String extractUsername(String token, TokenType type) {
        return extractClaims(token, type).getSubject();
    }

    public boolean isValid(String token, TokenType type, UserDetails user) {
        Claims claims = extractClaims(token, type);
        boolean base = hasValidBaseClaims(claims, user);
        if (type == RESET_TOKEN)
            return base && isStoredResetToken(claims, user);

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

    public boolean consumeResetToken(String token, UserDetails user) {
        Claims claims = extractClaims(token, RESET_TOKEN);
        if (!hasValidBaseClaims(claims, user) || !isStoredResetToken(claims, user)) {
            return false;
        }

        return Boolean.TRUE.equals(this.redisTemplate.delete(resetTokenKey(claims.getId())));
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

    private boolean hasValidBaseClaims(Claims claims, UserDetails user) {
        return claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private boolean isStoredResetToken(Claims claims, UserDetails user) {
        String tokenId = claims.getId();
        if (tokenId == null) {
            return false;
        }

        try {
            Object storedUsername = this.redisTemplate.opsForValue().get(resetTokenKey(tokenId));
            return storedUsername != null && storedUsername.toString().equals(user.getUsername());
        } catch (Exception e) {
            log.warn("Redis unavailable during reset token validation for user [{}]: {}", claims.getSubject(), e.getMessage());
            return false;
        }
    }

    private String resetTokenKey(String tokenId) {
        return RESET_TOKEN_KEY_PREFIX + tokenId;
    }
}
