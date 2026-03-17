package com.mosquizto.api.repository;

import com.mosquizto.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndVerifyCode(Long id, String verifyCode);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);
}
