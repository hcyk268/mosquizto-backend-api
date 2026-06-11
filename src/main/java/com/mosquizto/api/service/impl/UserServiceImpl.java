package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.dto.response.UserSummaryResponse;
import com.mosquizto.api.exception.*;
import com.mosquizto.api.mapper.UserMapper;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.FollowRepository;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.RedisTokenService;
import com.mosquizto.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final String USER_DETAILS_CACHE = "userdetails";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;
    private final UserMapper userMapper;
    private final RedisTokenService redisTokenService;
    private final CacheManager cacheManager;
    private final FollowRepository followRepository;

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public long addUser(AddUserRequest request) {
        Role role = this.roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role must be not " + request.getRole()));

        if (this.checkUsernameExists(request.getUsername())) {
            throw new ConflictException(ErrorCode.DUPLICATE_USERNAME, "Username already exists");
        }
        if (this.checkEmailExists(request.getEmail())) {
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
        User user = this.userRepository.findActiveByIdAndVerifyCode(userId, verifyCode)
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.INVALID_VERIFICATION_CODE,
                        "Invalid verification code or user not found"));

        user.activate(verifyCode);
        this.save(user);
    }

    @Override
    public void saveVerifyCode(long userId, String verifyCode) {
        User user = this.userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.assignVerifyCode(verifyCode);
        this.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
        this.evictUserDetailsAfterCommit(user.getUsername());
    }

    @Override
    public PageResponse<UserResponse> getListUser(int page, int size) {
        Page<User> userPage = this.userRepository.findAllActive(PageRequest.of(page - 1, size));

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
        return this.userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User currentUser = this.currentUserProvider.getCurrentUser();

        User user = this.userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or already deleted"));

        if (!user.canDelete(currentUser)) {
            throw new AccessDeniedException("You do not have permission to delete this user");
        }

        user.delete(currentUser);
        this.userRepository.save(user);
        this.invalidateDeletedUserAfterCommit(user.getUsername());
    }

    @Override
    public PageResponse<UserSummaryResponse> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            // Không có keyword -> Lấy tất cả
            userPage = userRepository.findAll(pageable);
        } else if (keyword.trim().length() < 3) {
            // Từ khóa quá ngắn -> Dùng search chuỗi con cơ bản
            userPage = userRepository.findByUsernameContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            // Từ khóa đủ dài -> Dùng search gần giống (Trigram)
            userPage = userRepository.searchFuzzyByUsername(keyword.trim(), pageable);
        }

        List<UserSummaryResponse> content = userPage.getContent().stream()
                .map(userMapper::toSummaryResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserSummaryResponse>builder()
                .page(page)
                .size(size)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .items(content)
                .build();
    }

    @Override
    public UserSummaryResponse getUser(String username) {
        User user = this.userRepository.findActiveByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        User currentUser = this.currentUserProvider.getCurrentUser();
        boolean followed = currentUser.getId() != null
                && user.getId() != null
                && !currentUser.getId().equals(user.getId())
                && this.followRepository.findActiveByFollowerAndFollowing(currentUser.getId(), user.getId()).isPresent();
        long followersCount = this.followRepository.countActiveFollowers(user.getId());
        long followingCount = this.followRepository.countActiveFollowing(user.getId());

        return this.userMapper.toSummaryResponse(user, followed, followersCount, followingCount);
    }

    private void evictUserDetailsAfterCommit(String username) {
        this.runAfterCommit(() -> this.evictUserDetails(username));
    }

    private void invalidateDeletedUserAfterCommit(String username) {
        this.runAfterCommit(() -> {
            this.evictUserDetails(username);
            this.redisTokenService.deleteById(username);
        });
    }

    private void evictUserDetails(String username) {
        Cache userDetailsCache = this.cacheManager.getCache(USER_DETAILS_CACHE);
        if (userDetailsCache != null) {
            userDetailsCache.evict(username);
        }
    }

    private void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
