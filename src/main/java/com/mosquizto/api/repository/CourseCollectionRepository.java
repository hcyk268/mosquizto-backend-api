package com.mosquizto.api.repository;

import com.mosquizto.api.model.CourseCollection;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCollectionRepository extends JpaRepository<CourseCollection, Long> {

    @Query("SELECT COUNT(cc) > 0 FROM CourseCollection cc " +
            "WHERE cc.course.id = :courseId " +
            "AND cc.collection.id = :collectionId " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
            "AND cc.collection.deletedAt IS NULL")
    Boolean existsActiveByCourseIdAndCollectionId(
            @Param("courseId") Long courseId,
            @Param("collectionId") Integer collectionId);

    @Query("SELECT cc FROM CourseCollection cc " +
            "WHERE cc.course.id = :courseId " +
            "AND cc.collection.id = :collectionId " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
            "AND cc.collection.deletedAt IS NULL")
    Optional<CourseCollection> findActiveByCourseIdAndCollectionId(
            @Param("courseId") Long courseId,
            @Param("collectionId") Integer collectionId);

    @Query("SELECT COALESCE(MAX(cc.orderIndex), 0) " +
            "FROM CourseCollection cc " +
            "WHERE cc.course.id = :courseId " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
            "AND cc.collection.deletedAt IS NULL")
    Integer findMaxActiveOrderIndex(@Param("courseId") Long courseId);

    @Query("SELECT cc FROM CourseCollection cc " +
            "JOIN FETCH cc.collection " +
            "WHERE cc.course.id = :courseId " +
            "AND cc.accessStatus = :accessStatus " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
            "AND cc.collection.deletedAt IS NULL " +
            "ORDER BY cc.orderIndex ASC, cc.id ASC")
    List<CourseCollection> findActiveByCourseIdAndStatus(
            @Param("courseId") Long courseId,
            @Param("accessStatus") AccessStatus accessStatus);
}
