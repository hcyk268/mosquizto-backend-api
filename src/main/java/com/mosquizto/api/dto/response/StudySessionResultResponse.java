package com.mosquizto.api.dto.response;

import lombok.*;

import java.io.Serializable;


@Builder
@Getter
public class StudySessionResultResponse implements Serializable {
    private Long sessionId;
    private Integer totalScore;
    private Integer totalCorrect;
    private Integer totalWrong;
    private Double accuracyRate;
    private Long durationMs;
}
