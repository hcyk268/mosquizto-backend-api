package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class StarredCollectionItemResponse implements Serializable {
    private Integer itemId;
    private Integer collectionId;
    private String term;
    private String definition;
    private String imageUrl;
    private Integer orderIndex;
    private Date starredAt;
}
