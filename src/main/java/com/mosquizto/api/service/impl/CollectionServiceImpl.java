package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionDocument;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    @Transactional // Đảm bảo 2 save thành công
    public Integer addCollection(CollectionRequest request) {
        User user = this.currentUserProvider.getCurrentUser();
        Collection collection = this.collectionMapper.toEntity(request, user);

        collection.setCount(0);
        Collection savedCollection = this.collectionRepository.save(collection);

        // Cập nhật thêm role cho người tạo
        UserCollectionId ucId = UserCollectionId.builder()
                .userId(user.getId())
                .collectionId(savedCollection.getId())
                .build();

        // 3. Khởi tạo đối tượng UserCollection
        UserCollection userCollection = UserCollection.builder()
                .id(ucId)
                .user(user)
                .collection(savedCollection)
                .role(CollectionRole.OWNER)
                .accessStatus(AccessStatus.ENABLE)
                .build();

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
        userCollectionService.updateLastOpenedAt(this.currentUserProvider.getCurrentUser().getId(), id);
        return this.collectionMapper.toResponse(collection);
    }

    @Override
    public void updateCollection(Integer id, CollectionRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        CollectionRole role = this.userCollectionRepository.getActiveRoleInUserCollection(user.getId(), collection.getId())
                .orElse(null);

        if (role == null || role == CollectionRole.VIEWER) {
            throw new InvalidDataException("Only editor and owner can edit this collection");
        }

        this.collectionMapper.updateEntity(collection, request);
        var updatedCollection = collectionRepository.save(collection);
        collectionSearchService.upsert(updatedCollection);
    }

    @Override
    public void deleteCollection(Integer id) {
        User user = currentUserProvider.getCurrentUser();

        CollectionRole role = this.userCollectionRepository.getActiveRoleInUserCollection(user.getId(), id)
                .orElse(null);

        if (role != CollectionRole.OWNER) {
            throw new InvalidDataException("Only the owner can delete this collection");
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
}