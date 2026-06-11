package com.mosquizto.api.repository;

import com.mosquizto.api.model.Follow;
import com.mosquizto.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT fl FROM Follow fl " +
            "WHERE fl.follower.id = :followerId " +
            "AND fl.following.id = :followingId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL")
    Optional<Follow> findByFollowerAndFollowing(@Param("followerId") Long follower, @Param("followingId") Long following);

    @Query("SELECT fl FROM Follow fl " +
            "WHERE fl.follower.id = :followerId " +
            "AND fl.following.id = :followingId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL")
    Optional<Follow> findActiveByFollowerAndFollowing(@Param("followerId") Long follower, @Param("followingId") Long following);

    @Query("SELECT COUNT(fl) FROM Follow fl " +
            "WHERE fl.following.id = :userId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL")
    long countActiveFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(fl) FROM Follow fl " +
            "WHERE fl.follower.id = :userId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL")
    long countActiveFollowing(@Param("userId") Long userId);

    @Query(value = "SELECT fl.follower FROM Follow fl " +
            "WHERE fl.following.id = :userId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL " +
            "ORDER BY fl.createdAt DESC",
            countQuery = "SELECT COUNT(fl) FROM Follow fl " +
                    "WHERE fl.following.id = :userId " +
                    "AND fl.follower.deletedAt IS NULL " +
                    "AND fl.following.deletedAt IS NULL " +
                    "AND fl.deletedAt IS NULL")
    Page<User> findActiveFollowers(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT fl.following FROM Follow fl " +
            "WHERE fl.follower.id = :userId " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL " +
            "ORDER BY fl.createdAt DESC",
            countQuery = "SELECT COUNT(fl) FROM Follow fl " +
                    "WHERE fl.follower.id = :userId " +
                    "AND fl.follower.deletedAt IS NULL " +
                    "AND fl.following.deletedAt IS NULL " +
                    "AND fl.deletedAt IS NULL")
    Page<User> findActiveFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT fl.following.id FROM Follow fl " +
            "WHERE fl.follower.id = :followerId " +
            "AND fl.following.id IN :followingIds " +
            "AND fl.follower.deletedAt IS NULL " +
            "AND fl.following.deletedAt IS NULL " +
            "AND fl.deletedAt IS NULL")
    List<Long> findActiveFollowingIds(@Param("followerId") Long followerId,
                                      @Param("followingIds") List<Long> followingIds);
}
