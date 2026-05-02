package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class FolderSummaryResponse implements Serializable {

    private Long id;

    private String name;
}
