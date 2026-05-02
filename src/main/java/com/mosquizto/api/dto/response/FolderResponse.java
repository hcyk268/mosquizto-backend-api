package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class FolderResponse implements Serializable {

    private Long id;

    private String name;

    private String description;

    private List<CollectionSummaryResponse> collections;

}
