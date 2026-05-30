package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.InvalidTokenException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
@RequiredArgsConstructor
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

    private final UserRepository userRepository;

    private User cachedUser;

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
        if (cachedUser == null) {
            cachedUser = userRepository.findByUsername(getCurrentUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + getCurrentUsername()));
        }

        return cachedUser;
    }
}
