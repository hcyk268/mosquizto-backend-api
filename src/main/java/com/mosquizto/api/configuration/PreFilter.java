package com.mosquizto.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mosquizto.api.dto.response.ErrorResponseException;
import com.mosquizto.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

import static com.mosquizto.api.util.TokenType.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Component
public class PreFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);

        if (StringUtils.isEmpty(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring("Bearer ".length());

        try {
            String userName = jwtService.extractUsername(accessToken, ACCESS_TOKEN);

            if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (jwtService.isValid(accessToken, ACCESS_TOKEN, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (ExpiredJwtException e) {
            log.error("Token has expired: {}", e.getMessage());
            sendErrorResponse(request, response, "Token has expired");
            return;
        } catch (MalformedJwtException e) {
            log.error("Invalid token format: {}", e.getMessage());
            sendErrorResponse(request, response, "Invalid token format");
            return;
        } catch (SignatureException e) {
            log.error("Invalid token signature: {}", e.getMessage());
            sendErrorResponse(request, response, "Invalid token signature");
            return;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            sendErrorResponse(request, response, "Unsupported token");
            return;
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            sendErrorResponse(request, response, "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseException errorResponse = new ErrorResponseException();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage(message);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
