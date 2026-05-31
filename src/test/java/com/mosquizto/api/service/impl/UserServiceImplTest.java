package com.mosquizto.api.service.impl;

import com.mosquizto.api.mapper.UserMapper;
import com.mosquizto.api.model.Role;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.RoleRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.RedisTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache userDetailsCache;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(
                this.userRepository,
                this.roleRepository,
                this.passwordEncoder,
                this.currentUserProvider,
                this.userMapper,
                this.redisTokenService,
                this.cacheManager
        );
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void shouldEvictUserDetailsWhenSavingUser() {
        User user = this.user(2L, "member", "USER");
        when(this.cacheManager.getCache("userdetails")).thenReturn(this.userDetailsCache);

        this.userService.save(user);

        verify(this.userRepository).save(user);
        verify(this.userDetailsCache).evict("member");
    }

    @Test
    void shouldInvalidateDeletedUserAfterTransactionCommit() {
        User admin = this.user(1L, "admin", "ADMIN");
        User user = this.user(2L, "member", "USER");
        when(this.currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(this.userRepository.findActiveById(2L)).thenReturn(Optional.of(user));
        when(this.cacheManager.getCache("userdetails")).thenReturn(this.userDetailsCache);
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();

        this.userService.deleteUser(2L);

        assertNotNull(user.getDeletedAt());
        assertSame(admin, user.getDeletedBy());
        verify(this.userRepository).save(user);
        verify(this.userDetailsCache, never()).evict("member");
        verify(this.redisTokenService, never()).deleteById("member");

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

        verify(this.userDetailsCache).evict("member");
        verify(this.redisTokenService).deleteById("member");
    }

    private User user(Long id, String username, String roleName) {
        User user = User.builder()
                .username(username)
                .role(Role.builder().name(roleName).build())
                .build();
        user.setId(id);
        return user;
    }
}
