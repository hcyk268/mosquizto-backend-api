package com.mosquizto.api.util;

import com.mosquizto.api.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public final class AuthorizationHeaderUtils {

    private static final String BEARER_PREFIX = "Bearer ";

    public static String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    public static String extractRequiredBearerToken(HttpServletRequest request) {
        String token = extractBearerToken(request);
        if (token == null) {
            throw new InvalidTokenException("Token is required");
        }

        return token;
    }
}
