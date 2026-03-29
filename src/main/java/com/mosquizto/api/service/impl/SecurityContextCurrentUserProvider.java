package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.InvalidTokenException;
import com.mosquizto.api.model.User;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

    private final UserService userService;

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidTokenException("User is not authenticated");
        }

        return authentication.getName();
    }

    @Override
    public User getCurrentUser() {
        return userService.getByUsername(getCurrentUsername());
    }
}
