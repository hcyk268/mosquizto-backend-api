package com.mosquizto.api.service.impl;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.service.AuthenticatedUserService;
import com.mosquizto.api.service.JwtService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.AuthorizationHeaderUtils;
import com.mosquizto.api.util.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class AuthenticatedUserServiceImpl implements AuthenticatedUserService {
    final private UserService userService;
    final private JwtService jwtService;
    @Override
    public User getAuthenticatedUser(HttpServletRequest httpServletRequest) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(httpServletRequest);
        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        return userService.getByUsername(username);
    }

    @Override
    public Boolean isAuthorOfCollection(HttpServletRequest httpServletRequest, Collection collection) {
        String token = httpServletRequest.getHeader(AUTHORIZATION).substring(7);
        String currentUsername = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        return currentUsername.equals(collection.getCreatedBy().getUsername());
    }

}
