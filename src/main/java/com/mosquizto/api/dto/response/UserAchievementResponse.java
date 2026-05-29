package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class UserAchievementResponse implements Serializable {
    private String code;
    private String title;
    private String description;
    private Boolean achieved;
    private Long progress;
    private Long target;
}
