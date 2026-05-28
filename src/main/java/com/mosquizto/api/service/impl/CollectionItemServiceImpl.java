package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionItemMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionItemService;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserCollectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CollectionItemServiceImpl implements CollectionItemService {

    private final CollectionItemRepository collectionItemRepository;
    private final CollectionRepository collectionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CollectionItemMapper collectionItemMapper;
    private final UserCollectionRepository userCollectionRepository;
    private final UserCollectionService userCollectionService;
    private final CollectionSearchService collectionSearchService;

    @Override
    @Transactional
    public CollectionItemResponse addNewItem(CollectionItemRequest request) {
        Collection collection = findCollectionById(request.getCollectionId());
        UserCollection membership = getMembership(collection.getId());

        if (!collection.canEdit(membership)) {
            throw new InvalidDataException("Only editor and owner can add items to this collection");
        }

        CollectionItem newItem = this.collectionItemMapper.toEntity(request, collection);
        collection.increaseItemCount();
        collectionRepository.save(collection);
        collectionSearchService.upsert(collection);
        return this.collectionItemMapper.toResponse(this.collectionItemRepository.save(newItem));
    }

    @Override
    public List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId) {
        Collection collection = findCollectionById(collectionId);
        User currentUser = this.currentUserProvider.getCurrentUser();
        UserCollection membership = getMembership(collectionId);

        if (!collection.canView(currentUser, membership)) {
            throw new InvalidDataException("You do not have permission to see this collection");
        }

        List<CollectionItem> items = this.collectionItemRepository.findByCollectionId(collectionId);
        this.userCollectionService.updateLastOpenedAt(currentUser.getId(), collectionId);
        return items.stream()
                .map(this.collectionItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CollectionItemResponse deleteCollectionItem(Integer id) {
        CollectionItem targetItem = getItemById(id);
        Collection collection = targetItem.getCollection();
        UserCollection membership = getMembership(collection.getId());

        if (!collection.canEdit(membership)) {
            throw new InvalidDataException("Only editor and owner can delete items in this collection");
        }

        this.collectionItemRepository.delete(targetItem);
        collection.decreaseItemCount();
        collectionRepository.save(collection);
        collectionSearchService.upsert(collection);
        return this.collectionItemMapper.toResponse(targetItem);
    }

    @Override
    @Transactional
    public CollectionItemResponse updateCollectionItem(Integer id, CollectionItemRequest request) {
        CollectionItem targetItem = getItemById(id);
        Collection collection = targetItem.getCollection();

        if (!collection.getId().equals(request.getCollectionId())) {
            throw new InvalidDataException("Item does not belong to this collection");
        }

        UserCollection membership = getMembership(collection.getId());
        if (!collection.canEdit(membership)) {
            throw new InvalidDataException("Only editor and owner can edit items in this collection");
        }

        this.collectionItemMapper.updateEntity(targetItem, request);
        CollectionItem savedItem = collectionItemRepository.save(targetItem);
        collectionSearchService.upsert(collection);

        return this.collectionItemMapper.toResponse(savedItem);
    }

    private Collection findCollectionById(Integer collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    }

    private CollectionItem getItemById(Integer id) {
        return collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    private UserCollection getMembership(Integer collectionId) {
        User currentUser = currentUserProvider.getCurrentUser();
        return userCollectionRepository.findByUserIdAndCollectionId(currentUser.getId(), collectionId)
                .orElse(null);
    }
}
