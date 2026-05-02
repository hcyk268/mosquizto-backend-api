package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class CollectionSummaryResponse implements Serializable {

    private Integer id;

    private String title;

    private Integer orderIndex;

}
