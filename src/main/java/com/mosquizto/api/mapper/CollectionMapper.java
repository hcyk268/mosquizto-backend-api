package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionReport;
import com.mosquizto.api.model.User;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public Collection toEntity(CollectionRequest request, User createdBy) {
        return Collection.initialize(
                createdBy,
                request.getTitle(),
                request.getDescription(),
                request.getVisibility()
        );
    }

    public void updateEntity(Collection collection, CollectionRequest request) {
        collection.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getVisibility()
        );
    }

    public CollectionResponse toResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .visibility(collection.getVisibility())
                .userId(collection.getCreatedBy() != null ? collection.getCreatedBy().getId() : null)
                .userName(collection.getCreatedBy() != null ? collection.getCreatedBy().getUsername() : null)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .count(collection.getItemCount())
                .build();
    }
    public Collection toEntity(CollectionResponse collectionResponse, User createdBy)
    {
        return Collection.initialize(createdBy,collectionResponse.getTitle(),
                collectionResponse.getDescription(),collectionResponse.getVisibility());

    }
    public CollectionReportResponse toResponse(CollectionReport report) {
        return CollectionReportResponse.builder()
                .id(report.getId())
                .collectionId(report.getCollection().getId())
                .reporterId(report.getReporter().getId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
