package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.AuthenticatedUserService;
import com.mosquizto.api.service.CollectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final AuthenticatedUserService authenticatedUserService ;
    @Override
    public Integer addCollection(CollectionRequest request, HttpServletRequest httpServletRequest) {
        User user = authenticatedUserService.getAuthenticatedUser(httpServletRequest);

        System.out.println(user.getId() + " " + user.getUsername());
        Collection collection = Collection.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .createdBy(user)
                .build();

        return collectionRepository.save(collection).getId();
    }

    @Override
    public PageResponse<CollectionResponse> getMyCollections(int page, int size, HttpServletRequest request) {
        User user = authenticatedUserService.getAuthenticatedUser(request);

        Page<Collection> collections = collectionRepository.findAllByCreatedById(user.getId(), PageRequest.of(page - 1, size));
        List<CollectionResponse> items = collections.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<CollectionResponse>builder()
                .page(page)
                .size(size)
                .totalElements(collections.getTotalElements())
                .totalPages(collections.getTotalPages())
                .items(items)
                .build();
    }

    @Override
    public CollectionResponse getDetail(Integer id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        return mapToResponse(collection);
    }

    @Override
    public void updateCollection(Integer id, CollectionRequest request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        collection.setTitle(request.getTitle());
        collection.setDescription(request.getDescription());
        collection.setVisibility(request.getVisibility());
        collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(Integer id) {
        collectionRepository.deleteById(id);
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

    private CollectionResponse mapToResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .visibility(collection.getVisibility())
                .userId(collection.getCreatedBy().getId())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }
}