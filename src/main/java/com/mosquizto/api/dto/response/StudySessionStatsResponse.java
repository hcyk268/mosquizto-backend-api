package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class StudySessionStatsResponse implements Serializable {
    private Integer collectionId;
    private String collectionName;
    private Long totalSessions;
    private Integer totalCorrect;
    private Integer totalWrong;
    private Double averageAccuracyRate;
    private Integer bestScore;
    private Long averageDurationMs;
    private Date lastStudiedAt;
}
