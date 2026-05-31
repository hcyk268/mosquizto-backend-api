package com.mosquizto.api.repository;

import com.mosquizto.api.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Integer> {
    @Query("select item from CollectionItem item " +
            "where item.id = :id " +
            "and item.deletedAt is null " +
            "and item.collection.deletedAt is null")
    Optional<CollectionItem> findActiveById(@Param("id") Integer id);

    @Query("select item from CollectionItem item " +
            "where item.collection.id = :collectionId " +
            "and item.deletedAt is null " +
            "and item.collection.deletedAt is null")
    List<CollectionItem> findAllActiveByCollectionId(@Param("collectionId") Integer collectionId);

    @Query("select item from CollectionItem item " +
            "where item.collection.id = :collectionId " +
            "and item.term = :term " +
            "and item.deletedAt is null " +
            "and item.collection.deletedAt is null")
    Optional<CollectionItem> findActiveByCollectionIdAndTerm(
            @Param("collectionId") Integer collectionId,
            @Param("term") String term);

    @Query("select item from CollectionItem item " +
            "where item.collection.id = :collectionId " +
            "and item.definition = :definition " +
            "and item.deletedAt is null " +
            "and item.collection.deletedAt is null")
    Optional<CollectionItem> findActiveByCollectionIdAndDefinition(
            @Param("collectionId") Integer collectionId,
            @Param("definition") String definition);
}
