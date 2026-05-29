package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class UserStreakResponse implements Serializable {
    private Integer currentStreakDays;
    private Integer longestStreakDays;
    private Integer totalStudyDays;
    private Long totalStudySessions;
    private Long completedStudySessions;
    private Date lastStudiedAt;
    private Boolean studiedToday;
    private Integer nextMilestoneDays;
}
