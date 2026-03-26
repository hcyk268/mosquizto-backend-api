package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.model.CollectionItem;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CollectionItemService {
    CollectionItemResponse addNewItem(CollectionItemRequest request, HttpServletRequest httpServletRequest);
    // cái này ko phân biệt người tạo :v
    List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId, HttpServletRequest httpServletRequest);

    CollectionItemResponse deleteCollectionItem(Integer id , HttpServletRequest httpServletRequest);

        CollectionItemResponse updateCollectionItem(Integer id , CollectionItemRequest request,  HttpServletRequest httpServletRequest);
}
