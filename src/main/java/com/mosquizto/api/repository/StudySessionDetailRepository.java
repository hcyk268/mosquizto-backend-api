package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySessionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudySessionDetailRepository extends JpaRepository<StudySessionDetail, Long> {
    List<StudySessionDetail> findAllByStudySessionIdOrderByIdAsc(Long studySessionId);
}
