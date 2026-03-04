package com.mosquizto.api.service;

import com.mosquizto.api.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);
}
