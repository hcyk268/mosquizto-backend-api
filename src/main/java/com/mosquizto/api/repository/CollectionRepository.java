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

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {

    @Query("SELECT c FROM Collection c " +
            "WHERE c.id = :id " +
            "AND c.deletedAt IS NULL")
    Optional<Collection> findActiveById(@Param("id") Integer id);

    @Query("SELECT c FROM Collection c " +
            "WHERE c.createdBy.id = :userId " +
            "AND c.deletedAt IS NULL " +
            "AND c.createdBy.deletedAt IS NULL")
    Page<Collection> findAllActiveByCreatorId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Collection c " +
            "WHERE c.deletedAt IS NULL " +
            "AND c.createdBy.deletedAt IS NULL")
    Page<Collection> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Collection c " +
            "WHERE c.createdBy.id = :userId " +
            "AND c.deletedAt IS NULL " +
            "AND c.createdBy.deletedAt IS NULL")
    long countActiveByCreatorId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT c FROM Collection c " +
            "LEFT JOIN UserCollection uc ON c.id = uc.collection.id " +
            "AND uc.deletedAt IS NULL " +
            "AND uc.user.deletedAt IS NULL " +
            "WHERE (c.createdBy.id = :userId " +
            "OR (uc.user.id = :userId AND uc.accessStatus = :accessStatus)) " +
            "AND c.deletedAt IS NULL " +
            "AND c.createdBy.deletedAt IS NULL")
    Page<Collection> findAllAccessibleByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("accessStatus") AccessStatus accessStatus,
            Pageable pageable);

    default Page<Collection> findAllAccessibleCollections(Long userId, Pageable pageable) {
        return findAllAccessibleByUserIdAndStatus(userId, AccessStatus.ENABLE, pageable);
    }

    @Query("SELECT c FROM Collection c " +
            "WHERE c.visibility = true " +
            "AND c.deletedAt IS NULL " +
            "AND c.createdBy.deletedAt IS NULL")
    Page<Collection> findPublicCollections(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Collection c SET c.count = c.count + :delta WHERE c.id = :id AND c.deletedAt IS NULL")
    void updateItemCount(@Param("id") Integer id, @Param("delta") int delta);

    @Modifying
    @Transactional
    @Query("UPDATE Collection c SET c.count = (SELECT COUNT(ci) FROM CollectionItem ci " +
            "WHERE ci.collection.id = c.id AND ci.deletedAt IS NULL) " +
            "WHERE c.deletedAt IS NULL")
    void syncAllCounts();
}
