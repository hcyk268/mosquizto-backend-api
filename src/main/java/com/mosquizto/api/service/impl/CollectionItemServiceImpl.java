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
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionItemService;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.CollectionRole;
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
    private final UserCollectionRepository userCollectionRepository;
    private final UserCollectionService userCollectionService ;
    private final CollectionSearchService collectionSearchService ;
    @Override
    public CollectionItemResponse addNewItem(CollectionItemRequest request) {
        var collection = findCollectionById(request.getCollectionId());

        CollectionRole role = getUserRoleInCollection(collection.getId());
        if (role == null || role == CollectionRole.VIEWER) {
            throw new InvalidDataException("Only editor and owner can add items to this collection");
        }

        CollectionItem newItem = this.collectionItemMapper.toEntity(request, collection);
        collectionRepository.updateItemCount(collection.getId(), 1);
        collectionSearchService.upsert(collection);
        return this.collectionItemMapper.toResponse(this.collectionItemRepository.save(newItem));
    }

    @Override
    public List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId) {
        var collection = findCollectionById(collectionId);
        CollectionRole role = getUserRoleInCollection(collectionId);

        if (Boolean.FALSE.equals(collection.getVisibility()) && role == null) {
            throw new InvalidDataException("You do not have permission to see this collection");
        }

        var items = this.collectionItemRepository.findByCollectionId(collectionId);
        var userId = this.currentUserProvider.getCurrentUser().getId() ;
        this.userCollectionService.updateLastOpenedAt(userId,collectionId);
        return items.stream()
                .map(this.collectionItemMapper::toResponse)
                .toList();
    }

    @Override
    public CollectionItemResponse deleteCollectionItem(Integer id) {
        CollectionItem targetItem = getItemById(id);
        Collection collection = targetItem.getCollection();

        CollectionRole role = getUserRoleInCollection(collection.getId());
        if (role == null || role == CollectionRole.VIEWER) {
            throw new InvalidDataException("Only editor and owner can delete items in this collection");
        }

        this.collectionItemRepository.delete(targetItem);
        collectionRepository.updateItemCount(collection.getId(), -1);
        collectionSearchService.upsert(collection);
        return this.collectionItemMapper.toResponse(targetItem);
    }

    @Override
    public CollectionItemResponse updateCollectionItem(Integer id, CollectionItemRequest request) {
        var collection = findCollectionById(request.getCollectionId());

        CollectionRole role = getUserRoleInCollection(collection.getId());
        if (role == null || role == CollectionRole.VIEWER) {
            throw new InvalidDataException("Only editor and owner can edit items in this collection");
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
    
    private CollectionRole getUserRoleInCollection(Integer collectionId) {
        User currentUser = currentUserProvider.getCurrentUser();
        return userCollectionRepository.getActiveRoleInUserCollection(currentUser.getId(), collectionId)
                .orElse(null); // Trả về null nếu user không thuộc collection này
    }
}