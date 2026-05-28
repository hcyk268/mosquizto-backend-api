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
        CollectionItem item = new CollectionItem();
        item.updateContent(
                request.getTerm(),
                request.getDefinition(),
                request.getImageUrl(),
                request.getOrderIndex()
        );
        item.assignTo(collection);
        return item;
    }

    public void updateEntity(CollectionItem item, CollectionItemRequest request) {
        item.updateContent(
                request.getTerm(),
                request.getDefinition(),
                request.getImageUrl(),
                request.getOrderIndex()
        );
    }

    public CollectionItemResponse toResponse(CollectionItem item) {
        return CollectionItemResponse.builder()
                .id(item.getId())
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
