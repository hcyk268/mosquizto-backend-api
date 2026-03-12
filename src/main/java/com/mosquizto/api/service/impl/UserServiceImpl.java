package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}
