package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionMembershipResolver;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CollectionMapper collectionMapper;
    private final UserCollectionRepository userCollectionRepository;
    private final UserCollectionService userCollectionService;
    private final CollectionSearchService collectionSearchService;
    private final CollectionMembershipResolver membershipResolver;

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
        Collection collection = this.collectionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        User user = this.currentUserProvider.getCurrentUser();
        membershipResolver.requireCanView(collection, user);

        userCollectionService.updateLastOpenedAt(user.getId(), id);
        return this.collectionMapper.toResponse(collection);
    }

    @Override
    public void updateCollection(Integer id, CollectionRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        membershipResolver.requireCanEdit(collection, user);

        this.collectionMapper.updateEntity(collection, request);
        var updatedCollection = collectionRepository.save(collection);
        collectionSearchService.upsert(updatedCollection);
    }

    @Override
    @Transactional
    public void deleteCollection(Integer id) {
        User user = currentUserProvider.getCurrentUser();
        Collection collection = this.collectionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        membershipResolver.requireCanDelete(collection, user);

        collection.delete(user);
        this.runAfterCommit(() -> this.collectionSearchService.delete(id));
    }

    @Override
    public Collection getById(Integer id) {
        return this.collectionRepository.findActiveById(id)
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
                .page(page)
                .size(size)
                .totalElements(collectionPage.getTotalElements())
                .totalPages(collectionPage.getTotalPages())
                .items(items)
                .build();
    }

    @Override
    public List<CollectionResponse> getRecentOpenedCollection() {
        var userId = currentUserProvider.getCurrentUser().getId();
        return userCollectionRepository.findRecentActiveByUserId(userId, PageRequest.of(0, 10))
                .stream()
                .filter(uc -> uc.getLastOpenedAt() != null) // Lọc an toàn
                .map(uc -> collectionMapper.toResponse(uc.getCollection()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isAccessible(Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        UserCollection membership = membershipResolver.getMembership(user.getId(), collectionId);
        return collection.canView(user, membership);
    }

    private void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
