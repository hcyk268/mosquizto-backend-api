package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.util.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
@Entity
public class User extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "user_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private UserStatus status;

    @Column(name = "verify_code", length = 50)
    private String verifyCode;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.mosquizto.api.model.Collection> collections = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserCollection> userCollections = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudySession> studySessions = new ArrayList<>();

    public static User register(String fullName,
                                String email,
                                String username,
                                String encodedPassword,
                                Role role) {
        if (role == null) {
            throw new InvalidDataException("User role must not be null");
        }

        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new InvalidDataException("Encoded password must not be blank");
        }

        return User.builder()
                .fullName(fullName)
                .email(email)
                .username(username)
                .password(encodedPassword)
                .status(UserStatus.INACTIVE)
                .role(role)
                .build();
    }

    public void activate(String verifyCode) {
        if (this.verifyCode == null || verifyCode == null || !this.verifyCode.equals(verifyCode)) {
            throw new InvalidDataException("Invalid verification code or user not found");
        }

        activate();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        clearVerifyCode();
    }

    public void assignVerifyCode(String verifyCode) {
        if (verifyCode == null || verifyCode.isBlank()) {
            throw new InvalidDataException("Verify code must not be blank");
        }

        this.verifyCode = verifyCode;
    }

    public void clearVerifyCode() {
        this.verifyCode = null;
    }

    public void changePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new InvalidDataException("Encoded password must not be blank");
        }

        this.password = encodedPassword;
    }

    public void updateProfile(String fullName) {
        if (fullName != null) {
            this.fullName = fullName;
        }
    }

    public void applyGoogleProfile(String fallbackFullName, Role defaultRole) {
        if ((this.fullName == null || this.fullName.isBlank())
                && fallbackFullName != null && !fallbackFullName.isBlank()) {
            this.fullName = fallbackFullName;
        }

        if (this.role == null && defaultRole != null) {
            this.role = defaultRole;
        }

        activate();
    }

    public boolean canDelete(User user) {
        return user != null
                && user.getId() != null
                && (user.getId().equals(this.getId()) || user.getRole().getName().equals("ADMIN"));
    }

    public void delete(User userBy) {
       this.setDeletedAt(new Date());
       this.setDeletedBy(userBy);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(this.status);
    }
}

