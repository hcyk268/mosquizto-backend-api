package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public Collection toEntity(CollectionRequest request, User createdBy) {
        return Collection.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .createdBy(createdBy)
                .build();
    }

    public void updateEntity(Collection collection, CollectionRequest request) {
        collection.setTitle(request.getTitle());
        collection.setDescription(request.getDescription());
        collection.setVisibility(request.getVisibility());
    }

    public CollectionResponse toResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .visibility(collection.getVisibility())
                .userId(collection.getCreatedBy() != null ? collection.getCreatedBy().getId() : null)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }
}
