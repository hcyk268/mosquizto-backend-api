package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class BestLearntCollectionResponse implements Serializable {

    private Integer collectionId;

    private String collectionName;

    private Long studySessionCount;
}
