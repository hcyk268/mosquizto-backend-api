package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.util.AccessStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    @Query("""
        SELECT s FROM StudySession s
        WHERE s.id = :sessionId
          AND s.deletedAt IS NULL
    """)
    Optional<StudySession> findActiveById(@Param("sessionId") Long sessionId);

    @Query("""
        SELECT s FROM StudySession s
        WHERE s.user.id = :userId
          AND s.deletedAt IS NULL
    """)
    Page<StudySession> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT s FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND s.collection.id = :collectionId " +
            "AND s.deletedAt IS NULL " +
            "AND s.collection.deletedAt IS NULL")
    List<StudySession> findAllActiveByUserIdAndCollectionId(
            @Param("userId") Long userId,
            @Param("collectionId") Integer collectionId);

    @Query("""
        SELECT s FROM StudySession s
        WHERE s.user.id = :userId
          AND s.deletedAt IS NULL
        ORDER BY s.startedAt DESC
    """)
    List<StudySession> findAllByUserIdOrderByStartedAtDesc(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(s) FROM StudySession s
        WHERE s.user.id = :userId
          AND s.deletedAt IS NULL
    """)
    long countByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(s) FROM StudySession s
        WHERE s.user.id = :userId
          AND s.deletedAt IS NULL
          AND s.completedAt IS NOT NULL
    """)
    long countByUserIdAndCompletedAtIsNotNull(@Param("userId") Long userId);

    @Query("SELECT s FROM StudySession s " +
            "WHERE s.id IN (" +
            "  SELECT MAX(s2.id) FROM StudySession s2 " +
            "  WHERE s2.user.id = :userId " +
            "  AND s2.deletedAt IS NULL " +
            "  AND s2.collection.deletedAt IS NULL " +
            "  GROUP BY s2.collection.id" +
            ") AND s.completedAt is null " +
            "AND s.deletedAt IS NULL " +
            "AND s.collection.deletedAt IS NULL " +
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
            "AND s.deletedAt IS NULL " +
            "AND s.user.deletedAt IS NULL " +
            "AND c.deletedAt IS NULL " +
            "AND uc.deletedAt IS NULL " +
            "AND uc.course.deletedAt IS NULL " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
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
            "AND s.deletedAt IS NULL " +
            "AND s.user.deletedAt IS NULL " +
            "AND s.collection.deletedAt IS NULL " +
            "AND uc.deletedAt IS NULL " +
            "AND uc.course.deletedAt IS NULL " +
            "AND cc.deletedAt IS NULL " +
            "AND cc.course.deletedAt IS NULL " +
            "AND s.completedAt IS NOT NULL")
    Long countCompletedCourseStudySessions(@Param("courseId") Long courseId,
                                           @Param("memberStatus") AccessStatus memberStatus,
                                           @Param("courseCollectionStatus") AccessStatus courseCollectionStatus);

    @Query("SELECT COALESCE(SUM(s.totalCorrect), 0) FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND s.deletedAt IS NULL")
    Long sumTotalCorrectByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND s.deletedAt IS NULL " +
            "AND COALESCE(s.totalCorrect, 0) > 0 " +
            "AND COALESCE(s.totalWrong, 0) = 0")
    boolean existsPerfectSessionByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT CASE
            WHEN COUNT(st) > 0 THEN TRUE
            ELSE FALSE
        END
        FROM StudySession st
        WHERE st.id = :id
          AND st.user.id = :userId
          AND st.deletedAt IS NULL
    """)
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}


