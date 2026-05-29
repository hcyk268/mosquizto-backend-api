package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.CollectionReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class CollectionReportResponse implements Serializable {
    private Long id;
    private Integer collectionId;
    private Long reporterId;
    private String reason;
    private String description;
    private CollectionReportStatus status;
    private Date createdAt;
    private Date updatedAt;
}
