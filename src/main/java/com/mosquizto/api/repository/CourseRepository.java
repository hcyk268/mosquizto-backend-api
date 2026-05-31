package com.mosquizto.api.repository;

import com.mosquizto.api.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c " +
            "WHERE c.id = :courseId " +
            "AND c.deletedAt IS NULL")
    Optional<Course> findActiveById(@Param("courseId") Long courseId);

    @Query("SELECT c FROM Course c " +
            "WHERE c.visibility = true " +
            "AND c.deletedAt IS NULL")
    Page<Course> findPublicCourses(Pageable pageable);
}
