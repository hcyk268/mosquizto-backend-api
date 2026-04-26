package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySession;
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

    @Query("SELECT s, MAX(s.startedAt) \n" +
            "FROM StudySession s\n" +
            "WHERE s.user.id = :userId AND s.completedAt IS NULL\n" +
            "GROUP BY s.id\n" +
            "ORDER BY MAX(s.startedAt) DESC")
    List<StudySession> getJumpBackInStudySession(@Param("userId") Long userId) ;
}


