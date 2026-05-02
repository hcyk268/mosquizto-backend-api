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

    Boolean existsByCourseIdAndCollectionId(Long courseId, Integer collectionId);

    Optional<CourseCollection> findByCourseIdAndCollectionId(Long courseId, Integer collectionId);

    @Query("SELECT COALESCE(MAX(cc.orderIndex), 0) " +
            "FROM CourseCollection cc " +
            "WHERE cc.course.id = :courseId")
    Integer findMaxOrderIndexCollection(@Param("courseId") Long courseId);

    @Query("SELECT cc FROM CourseCollection cc " +
            "JOIN FETCH cc.collection " +
            "WHERE cc.course.id = :courseId " +
            "AND cc.accessStatus = :accessStatus " +
            "ORDER BY cc.orderIndex ASC, cc.id ASC")
    List<CourseCollection> findAllByCourseIdAndAccessStatusOrderByOrderIndex(@Param("courseId") Long courseId,
                                                                              @Param("accessStatus") AccessStatus accessStatus);
}
