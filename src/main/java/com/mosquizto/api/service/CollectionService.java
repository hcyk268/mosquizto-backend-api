package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.model.Collection;
import jakarta.servlet.http.HttpServletRequest;

public interface CollectionService {
    Integer addCollection(CollectionRequest request, HttpServletRequest httpServletRequest);
    PageResponse<CollectionResponse> getMyCollections(int page, int size, HttpServletRequest request);
    CollectionResponse getDetail(Integer id);
    void updateCollection(Integer id, CollectionRequest request);
    void deleteCollection(Integer id);
    Collection getById(Integer id);
    Collection save(Collection collection);
}