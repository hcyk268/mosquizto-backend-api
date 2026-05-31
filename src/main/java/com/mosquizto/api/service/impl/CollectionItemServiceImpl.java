package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionItemMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.CollectionItemService;
import com.mosquizto.api.service.CollectionMembershipResolver;
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
    private final UserCollectionService userCollectionService;
    private final CollectionSearchService collectionSearchService;
    private final CollectionMembershipResolver membershipResolver;

    @Override
    @Transactional
    public CollectionItemResponse addNewItem(CollectionItemRequest request) {
        Collection collection = findCollectionById(request.getCollectionId());
        User user = this.currentUserProvider.getCurrentUser();
        membershipResolver.requireCanEdit(collection, user);

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
        membershipResolver.requireCanView(collection, currentUser);

        List<CollectionItem> items = this.collectionItemRepository.findAllActiveByCollectionId(collectionId);
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
        User user = this.currentUserProvider.getCurrentUser();
        membershipResolver.requireCanEdit(collection, user);

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

        User user = this.currentUserProvider.getCurrentUser();
        membershipResolver.requireCanEdit(collection, user);

        this.collectionItemMapper.updateEntity(targetItem, request);
        CollectionItem savedItem = collectionItemRepository.save(targetItem);
        collectionSearchService.upsert(collection);

        return this.collectionItemMapper.toResponse(savedItem);
    }

    private Collection findCollectionById(Integer collectionId) {
        return collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    }

    private CollectionItem getItemById(Integer id) {
        return collectionItemRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
}
