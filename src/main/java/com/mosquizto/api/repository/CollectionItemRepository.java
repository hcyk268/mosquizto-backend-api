package com.mosquizto.api.repository;

import com.mosquizto.api.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Integer> {
    List<CollectionItem> findByCollectionId(Integer collectionId);

    Optional<CollectionItem> findByCollectionIdAndTerm(Integer collectionId, String term);

    Optional<CollectionItem> findByCollectionIdAndDefinition(Integer collectionId, String definition);
}
