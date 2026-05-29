package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class UserActivityResponse implements Serializable {
    private String type;
    private String title;
    private String description;
    private Date occurredAt;
    private Long sessionId;
    private Integer collectionId;
    private String collectionName;
    private Integer itemId;
}
