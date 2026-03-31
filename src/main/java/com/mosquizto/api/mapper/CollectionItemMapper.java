package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class CollectionItemMapper {

    public CollectionItem toEntity(CollectionItemRequest request, Collection collection) {
        return CollectionItem.builder()
                .term(request.getTerm())
                .definition(request.getDefinition())
                .imageUrl(request.getImageUrl())
                .orderIndex(request.getOrderIndex())
                .collection(collection)
                .build();
    }

    public void updateEntity(CollectionItem item, CollectionItemRequest request) {
        item.setTerm(request.getTerm());
        item.setDefinition(request.getDefinition());
        item.setImageUrl(request.getImageUrl());
        item.setOrderIndex(request.getOrderIndex());
    }

    public CollectionItemResponse toResponse(CollectionItem item) {
        return CollectionItemResponse.builder()
                .term(item.getTerm())
                .definition(item.getDefinition())
                .imageUrl(item.getImageUrl())
                .orderIndex(item.getOrderIndex())
                .collectionId(item.getCollection() != null ? item.getCollection().getId() : null)
                .createAt(toLocalDateTime(item.getCreatedAt()))
                .updateAt(toLocalDateTime(item.getUpdatedAt()))
                .build();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
