package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.AnswerResultResponse;
import com.mosquizto.api.dto.response.StudySessionDetailsResponse;
import com.mosquizto.api.dto.response.StudySessionDetailsResponse.StudySessionAnswerDetailResponse;
import com.mosquizto.api.dto.response.StudySessionResponse;
import com.mosquizto.api.dto.response.StudySessionResultResponse;
import com.mosquizto.api.dto.response.StudySessionStatsResponse;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.StudySessionDetail;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class StudySessionMapper {

    public AnswerResultResponse toAnswerResultResponse(StudySession studySession, boolean isCorrect, String correctAnswer) {
        return AnswerResultResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(correctAnswer)
                .sessionId(studySession.getId())
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .build();
    }

    public StudySessionResponse toResponse(StudySession studySession) {
        return StudySessionResponse.builder()
                .sessionId(studySession.getId())
                .collectionName(getCollectionName(studySession))
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .startedAt(studySession.getStartedAt())
                .completedAt(studySession.getCompletedAt())
                .build();
    }

    public StudySessionDetailsResponse toDetailsResponse(StudySession studySession, List<StudySessionDetail> details) {
        List<StudySessionAnswerDetailResponse> mappedDetails = details == null
                ? Collections.emptyList()
                : details.stream()
                .map(this::toAnswerDetailResponse)
                .toList();

        return StudySessionDetailsResponse.builder()
                .sessionId(studySession.getId())
                .collectionId(getCollectionId(studySession))
                .collectionName(getCollectionName(studySession))
                .startedAt(studySession.getStartedAt())
                .completedAt(studySession.getCompletedAt())
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .details(mappedDetails)
                .build();
    }

    public StudySessionAnswerDetailResponse toAnswerDetailResponse(StudySessionDetail detail) {
        CollectionItem collectionItem = detail.getCollectionItem();
        Boolean mode = getAnswerMode(detail);

        return StudySessionAnswerDetailResponse.builder()
                .detailId(detail.getId())
                .collectionItemId(collectionItem != null ? collectionItem.getId() : null)
                .mode(mode)
                .term(collectionItem != null ? collectionItem.getTerm() : null)
                .definition(collectionItem != null ? collectionItem.getDefinition() : null)
                .question(getQuestion(collectionItem, mode))
                .correctAnswer(getCorrectAnswer(collectionItem, mode))
                .isCorrect(detail.getIsCorrect())
                .responseTimeMs(detail.getResponseTimeMs())
                .build();
    }

    public StudySessionResultResponse toResultResponse(StudySession studySession, double accuracyRate, long durationMs) {
        return StudySessionResultResponse.builder()
                .sessionId(studySession.getId())
                .totalScore(studySession.getTotalScore())
                .totalCorrect(studySession.getTotalCorrect())
                .totalWrong(studySession.getTotalWrong())
                .accuracyRate(accuracyRate)
                .durationMs(durationMs)
                .build();
    }

    public StudySessionStatsResponse toStatsResponse(
            Collection collection,
            long totalSessions,
            int totalCorrect,
            int totalWrong,
            double averageAccuracyRate,
            int bestScore,
            long averageDurationMs,
            Date lastStudiedAt
    ) {
        return StudySessionStatsResponse.builder()
                .collectionId(collection != null ? collection.getId() : null)
                .collectionName(collection != null ? collection.getTitle() : null)
                .totalSessions(totalSessions)
                .totalCorrect(totalCorrect)
                .totalWrong(totalWrong)
                .averageAccuracyRate(averageAccuracyRate)
                .bestScore(bestScore)
                .averageDurationMs(averageDurationMs)
                .lastStudiedAt(lastStudiedAt)
                .build();
    }

    private Integer getCollectionId(StudySession studySession) {
        return studySession.getCollection() != null ? studySession.getCollection().getId() : null;
    }

    private String getCollectionName(StudySession studySession) {
        return studySession.getCollection() != null ? studySession.getCollection().getTitle() : null;
    }

    private Boolean getAnswerMode(StudySessionDetail detail) {
        return detail.getMode() != null ? detail.getMode() : Boolean.TRUE;
    }

    private String getQuestion(CollectionItem collectionItem, Boolean mode) {
        if (collectionItem == null) {
            return null;
        }

        return Boolean.FALSE.equals(mode) ? collectionItem.getDefinition() : collectionItem.getTerm();
    }

    private String getCorrectAnswer(CollectionItem collectionItem, Boolean mode) {
        if (collectionItem == null) {
            return null;
        }

        return Boolean.FALSE.equals(mode) ? collectionItem.getTerm() : collectionItem.getDefinition();
    }
}
