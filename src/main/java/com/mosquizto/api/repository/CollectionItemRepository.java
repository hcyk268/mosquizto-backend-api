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

    Optional<CollectionItem> findByCollectionIdAndTerm(@Param("collectionId") Integer collectionId,
            @Param("term") String term);
}
