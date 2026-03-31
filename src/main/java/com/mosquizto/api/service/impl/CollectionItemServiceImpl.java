package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionItemMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.CollectionItemService;
import com.mosquizto.api.service.CurrentUserProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CollectionItemServiceImpl implements CollectionItemService {
    private final CollectionItemRepository collectionItemRepository;
    private final CollectionRepository collectionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CollectionItemMapper collectionItemMapper;

    @Override
    public CollectionItemResponse addNewItem(CollectionItemRequest request) {
        var collection = findCollectionById(request.getCollectionId());
        if (!isCurrentUserAuthorOf(collection)) {
            throw new InvalidDataException("You do not have permission to add items to this collection");
        }

        CollectionItem newItem = this.collectionItemMapper.toEntity(request, collection);

        return this.collectionItemMapper.toResponse(this.collectionItemRepository.save(newItem));
    }

    @Override
    public List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId) {
        var collection = findCollectionById(collectionId);
        if (collection.getVisibility().equals(false)
                && !isCurrentUserAuthorOf(collection)) {
            throw new InvalidDataException("You do not have permission to see this collection");
        }
        var items = this.collectionItemRepository.findByCollectionId(collectionId);
        return items.stream()
                .map(this.collectionItemMapper::toResponse)
                .toList();
    }

    @Override
    public CollectionItemResponse deleteCollectionItem(Integer id) {
        CollectionItem targetItem = getItemById(id);
        Collection collection = targetItem.getCollection();
        if (!isCurrentUserAuthorOf(collection)) {
            throw new InvalidDataException("You do not have permission to delete this item");
        }

        this.collectionItemRepository.delete(targetItem);
        return this.collectionItemMapper.toResponse(targetItem);
    }

    @Override
    public CollectionItemResponse updateCollectionItem(Integer id, CollectionItemRequest request) {
        var collection = findCollectionById(request.getCollectionId());
        if (!isCurrentUserAuthorOf(collection)) {
            throw new InvalidDataException("You do not have permission to add items to this collection");
        }
        var targetItem = getItemById(id);
        this.collectionItemMapper.updateEntity(targetItem, request);
        return this.collectionItemMapper.toResponse(this.collectionItemRepository.save(targetItem));
    }

    private Collection findCollectionById(Integer collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    }

    private CollectionItem getItemById(Integer id) {
        return collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    private boolean isCurrentUserAuthorOf(Collection collection) {
        User currentUser = currentUserProvider.getCurrentUser();
        return collection.getCreatedBy() != null
                && collection.getCreatedBy().getId() != null
                && collection.getCreatedBy().getId().equals(currentUser.getId());
    }
}
