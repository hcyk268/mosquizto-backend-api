package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    Page<StudySession> findAllByUserId(Long userId, Pageable pageable);

    java.util.List<StudySession> findAllByUserIdAndCollectionId(Long userId, Integer collectionId);
}
