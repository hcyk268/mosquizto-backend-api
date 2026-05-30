package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CollectionMapper collectionMapper;
    private final UserCollectionRepository userCollectionRepository;
    private  final UserCollectionService userCollectionService ;
    private final CollectionSearchService collectionSearchService ;

    @Override
    @Transactional
    public Integer addCollection(CollectionRequest request) {
        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionMapper.toEntity(request, user);
        Collection savedCollection = this.collectionRepository.save(collection);

        UserCollection userCollection = UserCollection.createOwner(user, savedCollection);

        this.userCollectionRepository.save(userCollection);
        collectionSearchService.upsert(savedCollection);
        return savedCollection.getId();
    }

    @Override
    public PageResponse<CollectionResponse> getMyCollections(int page, int size) {
        User user = this.currentUserProvider.getCurrentUser();


        Page<Collection> collectionPage = this.collectionRepository.findAllAccessibleCollections(
                user.getId(), PageRequest.of(page - 1, size));

        List<CollectionResponse> items = collectionPage.getContent().stream()
                .map(this.collectionMapper::toResponse)
                .toList();

        return PageResponse.<CollectionResponse>builder()
                .page(page)
                .size(size)
                .totalElements(collectionPage.getTotalElements())
                .totalPages(collectionPage.getTotalPages())
                .items(items)
                .build();
    }

    @Override
    public CollectionResponse getDetail(Integer id) {
        Collection collection = this.collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        User user = this.currentUserProvider.getCurrentUser();
        if (!collection.canView(user, getMembership(user.getId(), collection.getId()))) {
            throw new AccessDeniedException("You do not have permission to view this collection");
        }

        userCollectionService.updateLastOpenedAt(user.getId(), id);
        return this.collectionMapper.toResponse(collection);
    }

    @Override
    public void updateCollection(Integer id, CollectionRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        UserCollection membership = getMembership(user.getId(), collection.getId());
        if (!collection.canEdit(membership)) {
            throw new AccessDeniedException("Only editor and owner can edit this collection");
        }

        this.collectionMapper.updateEntity(collection, request);
        var updatedCollection = collectionRepository.save(collection);
        collectionSearchService.upsert(updatedCollection);
    }

    @Override
    public void deleteCollection(Integer id) {
        User user = currentUserProvider.getCurrentUser();
        Collection collection = this.collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        UserCollection membership = getMembership(user.getId(), id);
        if (!collection.canDelete(membership)) {
            throw new AccessDeniedException("Only the owner can delete this collection");
        }
        collectionSearchService.delete(id);
        this.collectionRepository.deleteById(id);
    }

    @Override
    public Collection getById(Integer id) {
        return this.collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection Not Found"));
    }

    @Override
    public Collection save(Collection collection) {
        return this.collectionRepository.save(collection);
    }

    @Override
    public PageResponse<CollectionResponse> getAllPublicCollection(int page, int size) {
        var collectionPage = this.collectionRepository.findPublicCollections(PageRequest.of(page - 1, size));
        List<CollectionResponse> items = collectionPage.getContent().stream()
                .map(this.collectionMapper::toResponse)
                .toList();

        return PageResponse.<CollectionResponse>builder()
                .page(collectionPage.getTotalPages())
                .size(collectionPage.getSize())
                .totalElements(collectionPage.getTotalElements())
                .totalPages(collectionPage.getTotalPages())
                .items(items)
                .build();
    }

    @Override
    public List<CollectionResponse> getRecentOpenedCollection() {
        var userId = currentUserProvider.getCurrentUser().getId();
        return userCollectionRepository.findTop10ByUserIdOrderByLastOpenedAtDesc(userId)
                .stream()
                .filter(uc -> uc.getLastOpenedAt() != null) // Lọc an toàn
                .map(uc -> collectionMapper.toResponse(uc.getCollection()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isAccessible(Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        return collection.canView(user, getMembership(user.getId(), collectionId));
    }

    private UserCollection getMembership(Long userId, Integer collectionId) {
        return this.userCollectionRepository.findByUserIdAndCollectionId(userId, collectionId)
                .orElse(null);
    }
}
