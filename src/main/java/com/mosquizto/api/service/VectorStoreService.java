package com.mosquizto.api.service;

import com.mosquizto.api.model.Collection;

import java.util.List;

public interface VectorStoreService {
    public void ensureCollection() ;
    public void upsertCollection(Collection col, float[] vector);
    public List<Collection> getEmbeddedCollections() ;
    void deleteCollection(Integer collectionId);
}
