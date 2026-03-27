package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class StudySessionDetailsResponse implements Serializable {
    private Long sessionId;
    private Integer collectionId;
    private String collectionName;
    private Date startedAt;
    private Date completedAt;
    private Integer totalScore;
    private Integer totalCorrect;
    private Integer totalWrong;
    private List<StudySessionAnswerDetailResponse> details;
    
    @Getter
    @Builder
    public static class StudySessionAnswerDetailResponse implements Serializable {
        private Long detailId;
        private Integer collectionItemId;
        private String term;
        private String correctAnswer;
        private Boolean isCorrect;
        private Integer responseTimeMs;
    }
}
