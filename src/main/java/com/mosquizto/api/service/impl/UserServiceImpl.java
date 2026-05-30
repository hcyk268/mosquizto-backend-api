package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.exception.BusinessRuleException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.ErrorCode;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.UserMapper;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;
    private final UserMapper userMapper;

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public long addUser(AddUserRequest request) {
        Role role = this.roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role must be not " + request.getRole()));

        if (this.userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException(ErrorCode.DUPLICATE_USERNAME, "Username already exists");
        }
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL, "Email already exists");
        }

        User user = User.register(
                request.getFullName(),
                request.getEmail(),
                request.getUsername(),
                this.passwordEncoder.encode(request.getPassword()),
                role
        );

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
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.INVALID_VERIFICATION_CODE,
                        "Invalid verification code or user not found"));

        user.activate(verifyCode);
        this.userRepository.save(user);
    }

    @Override
    public void saveVerifyCode(long userId, String verifyCode) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.assignVerifyCode(verifyCode);
        this.userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }

    @Override
    public PageResponse<UserResponse> getListUser(int page, int size) {
        Page<User> userPage = this.userRepository.findAll(PageRequest.of(page - 1, size));

        List<UserResponse> items = userPage.getContent().stream()
                .map(userMapper::toResponse)
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
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword()))
            throw new BusinessRuleException(ErrorCode.PASSWORD_MISMATCH,
                    "New password must be different from old password");

        User user = this.currentUserProvider.getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BusinessRuleException(ErrorCode.PASSWORD_MISMATCH, "Old password wrong");
        }

        user.changePassword(this.passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        this.save(user);
    }

    @Override
    public UserResponse getProfile() {
        User user = this.currentUserProvider.getCurrentUser();

        return this.userMapper.toResponse(user);
    }

    @Override
    public void updateUser(UpdateUserRequest updateUserRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        user.updateProfile(updateUserRequest.getFullName());

        this.save(user);
    }

    @Override
    public User getById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
