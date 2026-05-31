package com.mosquizto.api.repository;

import com.mosquizto.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u " +
            "where u.id = :id " +
            "and u.deletedAt is null")
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("select u from User u " +
            "where u.username = :username " +
            "and u.deletedAt is null")
    Optional<User> findActiveByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("select u from User u " +
            "where u.id = :id " +
            "and u.verifyCode = :verifyCode " +
            "and u.deletedAt is null")
    Optional<User> findActiveByIdAndVerifyCode(
            @Param("id") Long id,
            @Param("verifyCode") String verifyCode);

    @Query("select u from User u " +
            "where u.email = :email " +
            "and u.deletedAt is null")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("select u from User u " +
            "where u.deletedAt is null")
    Page<User> findAllActive(Pageable pageable);
}
