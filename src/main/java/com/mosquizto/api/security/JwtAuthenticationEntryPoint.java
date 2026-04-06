package com.mosquizto.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mosquizto.api.dto.response.ErrorResponseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseException errorResponse = new ErrorResponseException();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Authentication is required to access this resource");

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
