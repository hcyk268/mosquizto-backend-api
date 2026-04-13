package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    Page<Collection> findAllByCreatedById(Long userId, Pageable pageable);
    @Procedure(procedureName = "seed_user_collections")
    void callSeedUserCollections(Integer p_user_id);
}