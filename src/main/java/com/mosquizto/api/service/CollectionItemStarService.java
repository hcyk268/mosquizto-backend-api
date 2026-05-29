package com.mosquizto.api.service;

import com.mosquizto.api.dto.response.StarredCollectionItemResponse;

import java.util.List;

public interface CollectionItemStarService {
    StarredCollectionItemResponse starItem(Integer itemId);

    void unstarItem(Integer itemId);

    List<StarredCollectionItemResponse> getMyStarredItems();
}
