package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {

    Page<Collection> findAllByCreatedById(Long userId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Collection c " +
            "LEFT JOIN UserCollection uc ON c.id = uc.collection.id " +
            "WHERE c.createdBy.id = :userId " +
            "OR (uc.user.id = :userId AND uc.accessStatus = :accessStatus)")
    Page<Collection> findAllAccessibleCollectionsByAccessStatus(
            @Param("userId") Long userId,
            @Param("accessStatus") AccessStatus accessStatus,
            Pageable pageable);

    default Page<Collection> findAllAccessibleCollections(Long userId, Pageable pageable) {
        return findAllAccessibleCollectionsByAccessStatus(userId, AccessStatus.ENABLE, pageable);
    }

    @Query("SELECT c FROM Collection c WHERE c.visibility = true")
    Page<Collection> findPublicCollections(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Collection c SET c.count = c.count + :delta WHERE c.id = :id")
    void updateItemCount(@Param("id") Integer id, @Param("delta") int delta);

    @Modifying
    @Transactional
    @Query("UPDATE Collection c SET c.count = (SELECT COUNT(ci) FROM CollectionItem ci WHERE ci.collection.id = c.id)")
    void syncAllCounts();
}
