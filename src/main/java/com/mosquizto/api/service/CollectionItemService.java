package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;

import java.util.List;

public interface CollectionItemService {
    CollectionItemResponse addNewItem(CollectionItemRequest request);
    List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId);
    CollectionItemResponse deleteCollectionItem(Integer id);
    CollectionItemResponse updateCollectionItem(Integer id, CollectionItemRequest request);
}
