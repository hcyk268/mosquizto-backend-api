package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
