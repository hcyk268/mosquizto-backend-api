package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class StudySessionResponse implements Serializable {
    private Long sessionId;
    private String collectionName;
    private Integer collectionId ;
    private Integer collectionCount ;
    private Integer totalScore;
    private Integer totalCorrect;
    private Integer totalWrong;
    private Date startedAt;
    private Date completedAt;
}
