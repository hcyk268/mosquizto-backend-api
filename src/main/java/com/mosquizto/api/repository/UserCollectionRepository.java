package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection, UserCollectionId> {

    @Query("select uc from UserCollection uc join fetch uc.user " +
            "where uc.collection.id = :collectionId " +
            "and uc.accessStatus = :accessStatus " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and uc.collection.deletedAt is null")
    List<UserCollection> findActiveMembersByCollectionIdAndStatus(
            @Param("collectionId") Integer collectionId,
            @Param("accessStatus") AccessStatus accessStatus);

    default List<UserCollection> findAllActiveMembersByCollectionId(Integer collectionId) {
        return findActiveMembersByCollectionIdAndStatus(collectionId, AccessStatus.ENABLE);
    }

    @Query("select uc.role from UserCollection uc " +
            "where uc.user.id = :userId " +
            "and uc.collection.id = :collectionId " +
            "and uc.accessStatus = :accessStatus " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and uc.collection.deletedAt is null")
    Optional<CollectionRole> findActiveRole(
            @Param("userId") Long userId,
            @Param("collectionId") Integer collectionId,
            @Param("accessStatus") AccessStatus accessStatus);

    default Optional<CollectionRole> getActiveRoleInUserCollection(Long userId, Integer collectionId) {
        return findActiveRole(userId, collectionId, AccessStatus.ENABLE);
    }

    @Query("select uc from UserCollection uc " +
            "where uc.accessStatus = :accessStatus " +
            "and uc.collection.id = :collectionId " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and uc.collection.deletedAt is null")
    List<UserCollection> findActiveByCollectionIdAndStatus(
            @Param("accessStatus") AccessStatus accessStatus,
            @Param("collectionId") Integer collectionId);

    @Query("select uc from UserCollection uc " +
            "join fetch uc.collection c " +
            "where uc.user.id = :userId " +
            "and uc.lastOpenedAt is not null " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and c.deletedAt is null " +
            "order by uc.lastOpenedAt desc")
    List<UserCollection> findRecentActiveByUserId(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("select uc from UserCollection uc " +
            "where uc.user.id = :userId " +
            "and uc.collection.id = :collectionId " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and uc.collection.deletedAt is null")
    Optional<UserCollection> findActiveByUserIdAndCollectionId(
            @Param("userId") Long userId,
            @Param("collectionId") Integer collectionId);

    @Query("select uc from UserCollection uc " +
            "where uc.id = :id " +
            "and uc.deletedAt is null " +
            "and uc.user.deletedAt is null " +
            "and uc.collection.deletedAt is null")
    Optional<UserCollection> findActiveById(@Param("id") UserCollectionId id);

    @Query("select count(uc) > 0 from UserCollection uc " +
            "where uc.id = :id " +
            "and uc.deletedAt is null")
    boolean existsActiveById(@Param("id") UserCollectionId id);
}
