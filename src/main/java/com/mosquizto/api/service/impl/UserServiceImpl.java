package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.JwtService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.AuthorizationHeaderUtils;
import com.mosquizto.api.util.TokenType;
import com.mosquizto.api.util.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public long addUser(AddUserRequest request) {
        Role role = this.roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role must be not " + request.getRole()));

        if (this.userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidDataException("Username already exists");
        }
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidDataException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.INACTIVE)
                .role(role)
                .build();

        this.userRepository.save(user);

        return user.getId();
    }

    @Override
    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public void confirmUser(long userId, String verifyCode) {
        User user = this.userRepository.findByIdAndVerifyCode(userId, verifyCode)
                .orElseThrow(() -> new InvalidDataException("Invalid verification code or user not found"));

        user.setStatus(UserStatus.ACTIVE);
        user.setVerifyCode(null);
        this.userRepository.save(user);
    }

    @Override
    public void saveVerifyCode(long userId, String verifyCode) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setVerifyCode(verifyCode);
        this.userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidDataException("User not found"));
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }

    @Override
    public PageResponse<UserResponse> getListUser(int page, int size) {
        Page<User> userPage = this.userRepository.findAll(PageRequest.of(page - 1, size));

        List<UserResponse> items = userPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .role(user.getRole() != null ? user.getRole().getName() : null)
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .toList();

        return PageResponse.<UserResponse>builder()
                .page(page)
                .size(size)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .items(items)
                .build();
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword()))
            throw new InvalidDataException("New password must be different from old password");

        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);

        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        var user = this.getByUsername(username);

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException("Old password wrong");
        }

        user.setPassword(this.passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        this.save(user);
    }

    @Override
    public UserResponse getProfile(HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        User user = this.getByUsername(username);

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

    @Override
    public void updateUser(UpdateUserRequest updateUserRequest, HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        User user = this.getByUsername(username);

        user.setFullName(updateUserRequest.getFullName());

        this.save(user);
    }

    @Override
    public User getById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
