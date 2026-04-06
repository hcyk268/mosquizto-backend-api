package com.mosquizto.api.security;

import com.mosquizto.api.service.TokenService;
import com.mosquizto.api.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.mosquizto.api.util.TokenType.*;

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

    private final TokenService tokenService;

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
        return base && this.tokenService.getByUsername(claims.getSubject()) != null;
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
