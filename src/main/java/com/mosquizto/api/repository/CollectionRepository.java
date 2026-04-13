package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    Page<Collection> findAllByCreatedById(Long userId, Pageable pageable);
}