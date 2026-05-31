package com.mosquizto.api.repository;

import com.mosquizto.api.model.StudySessionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudySessionDetailRepository extends JpaRepository<StudySessionDetail, Long> {

    @Query("select detail from StudySessionDetail detail " +
            "join fetch detail.collectionItem item " +
            "where detail.studySession.id = :studySessionId " +
            "and detail.deletedAt is null " +
            "and detail.studySession.deletedAt is null " +
            "order by detail.id asc")
    List<StudySessionDetail> findAllActiveByStudySessionId(@Param("studySessionId") Long studySessionId);
}
