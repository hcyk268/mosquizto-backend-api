package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserCourse;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    @Query(value = "SELECT uc FROM UserCourse uc " +
            "JOIN FETCH uc.course c " +
            "WHERE uc.user.id = :userId " +
            "AND uc.accessStatus = :accessStatus",
            countQuery = "SELECT COUNT(uc) FROM UserCourse uc " +
                    "WHERE uc.user.id = :userId " +
                    "AND uc.accessStatus = :accessStatus")
    Page<UserCourse> findAllByUserIdAndAccessStatusWithCourse(@Param("userId") Long userId,
                                                               @Param("accessStatus") AccessStatus accessStatus,
                                                               Pageable pageable);

    @Query(value = "SELECT uc FROM UserCourse uc " +
            "JOIN FETCH uc.user u " +
            "JOIN FETCH uc.course c " +
            "WHERE uc.course.id = :courseId " +
            "AND uc.accessStatus = :accessStatus",
            countQuery = "SELECT COUNT(uc) FROM UserCourse uc " +
                    "WHERE uc.course.id = :courseId " +
                    "AND uc.accessStatus = :accessStatus")
    Page<UserCourse> findAllWithStatus(@Param("courseId") Long courseId, @Param("accessStatus") AccessStatus accessStatus, Pageable pageable);
}
