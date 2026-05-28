package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.model.Collection;
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
}
