package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    Page<StudySession> findAllByUserId(Long userId, Pageable pageable);
    java.util.List<StudySession> findAllByUserIdAndCollectionId(Long userId, Integer collectionId);

    List<StudySession> findAllByUserIdOrderByStartedAtDesc(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndCompletedAtIsNotNull(Long userId);

    @Query("SELECT s FROM StudySession s " +
            "WHERE s.id IN (" +
            "  SELECT MAX(s2.id) FROM StudySession s2 " +
            "  WHERE s2.user.id = :userId  " +
            "  GROUP BY s2.collection.id" +
            ") AND s.completedAt is null " +
            "ORDER BY s.startedAt DESC")
    List<StudySession> getJumpBackInStudySession(@Param("userId") Long userId) ;

    @Query("SELECT s FROM StudySession s " +
            "JOIN FETCH s.collection c, UserCourse uc, CourseCollection cc " +
            "WHERE uc.user = s.user " +
            "AND uc.course.id = :courseId " +
            "AND uc.accessStatus = :memberStatus " +
            "AND cc.collection = c " +
            "AND cc.course.id = :courseId " +
            "AND cc.accessStatus = :courseCollectionStatus " +
            "AND s.completedAt IS NOT NULL")
    List<StudySession> findCompletedCourseStudySessions(@Param("courseId") Long courseId,
                                                        @Param("memberStatus") AccessStatus memberStatus,
                                                        @Param("courseCollectionStatus") AccessStatus courseCollectionStatus);

    @Query("SELECT COUNT(s) FROM StudySession s, UserCourse uc, CourseCollection cc " +
            "WHERE uc.user = s.user " +
            "AND uc.course.id = :courseId " +
            "AND uc.accessStatus = :memberStatus " +
            "AND cc.collection = s.collection " +
            "AND cc.course.id = :courseId " +
            "AND cc.accessStatus = :courseCollectionStatus " +
            "AND s.completedAt IS NOT NULL")
    Long countCompletedCourseStudySessions(@Param("courseId") Long courseId,
                                           @Param("memberStatus") AccessStatus memberStatus,
                                           @Param("courseCollectionStatus") AccessStatus courseCollectionStatus);

    @Query("SELECT COALESCE(SUM(s.totalCorrect), 0) FROM StudySession s WHERE s.user.id = :userId")
    Long sumTotalCorrectByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND COALESCE(s.totalCorrect, 0) > 0 " +
            "AND COALESCE(s.totalWrong, 0) = 0")
    boolean existsPerfectSessionByUserId(@Param("userId") Long userId);
}


