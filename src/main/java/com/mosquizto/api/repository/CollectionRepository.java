package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    Page<Collection> findAllByCreatedById(Long userId, Pageable pageable);
    @Query("SELECT DISTINCT c FROM Collection c " +
            "LEFT JOIN UserCollection uc ON c.id = uc.collection.id " +
            "WHERE c.createdBy.id = :userId OR uc.user.id = :userId")
    Page<Collection> findAllAccessibleCollections(@Param("userId") Long userId,Pageable pageable);

    @Query("SELECT c FROM Collection c WHERE c.visibility = true")
    Page<Collection> findPublicCollections( Pageable pageable);
}