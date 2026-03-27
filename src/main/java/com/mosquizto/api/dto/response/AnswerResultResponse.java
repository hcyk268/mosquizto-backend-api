package com.mosquizto.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class AnswerResultResponse implements Serializable {

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("total_score")
    private Integer totalScore;

    @JsonProperty("total_correct")
    private Integer totalCorrect;

    @JsonProperty("total_wrong")
    private Integer totalWrong;
}
