package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.model.Collection;
import java.awt.print.Pageable;

public interface CollectionService {
    Integer addCollection(CollectionRequest request);
    PageResponse<CollectionResponse> getMyCollections(int page, int size);
    CollectionResponse getDetail(Integer id);
    void updateCollection(Integer id, CollectionRequest request);
    void deleteCollection(Integer id);
    Collection getById(Integer id);
    Collection save(Collection collection);
    // Tương tác bên ngoài , ko thông qua user
    PageResponse<CollectionResponse> getAllPublicCollection(int page, int size);

}
