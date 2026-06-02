package com.mosquizto.api.service;


import com.mosquizto.api.model.Collection;

public interface EmbeddingService {

    public void init() ;

    public float[] embed(String text) ;

    public float[] embedCollection(Collection collection);
}
