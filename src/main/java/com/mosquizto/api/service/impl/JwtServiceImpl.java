package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.JwtService;
import com.mosquizto.api.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import static com.mosquizto.api.util.TokenType.ACCESS_TOKEN;
import static com.mosquizto.api.util.TokenType.REFRESH_TOKEN;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.expiryHour}")
    private int expiryHour;

    @Value("${jwt.expiryDay}")
    private int expiryDay;

    @Override
    public String generateAccessToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * expiryHour))
                .signWith(getKey(ACCESS_TOKEN))
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * expiryDay * 24))
                .signWith(getKey(REFRESH_TOKEN))
                .compact();
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaims(token, type).getSubject();
    }

    @Override
    public boolean isValid(String token, TokenType type, UserDetails user) {
        Claims claims = extractClaims(token, type);
        return claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private SecretKey getKey(TokenType type) {
        if (ACCESS_TOKEN.equals(type))
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        else
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
    }

    private Claims extractClaims(String token, TokenType type) {
        return Jwts.parser()
                .verifyWith(getKey(type))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
