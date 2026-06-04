package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.CollectionReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
