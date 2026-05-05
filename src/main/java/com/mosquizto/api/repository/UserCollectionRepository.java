package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
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
            "and uc.accessStatus = :accessStatus")
    List<UserCollection> findAllMembersByCollectionIdAndAccessStatus(
            @Param("collectionId") Integer collectionId,
            @Param("accessStatus") AccessStatus accessStatus);

    default List<UserCollection> findAllActiveMembersByCollectionId(Integer collectionId) {
        return findAllMembersByCollectionIdAndAccessStatus(collectionId, AccessStatus.ENABLE);
    }

    @Query("select uc.role from UserCollection uc " +
            "where uc.user.id = :userId " +
            "and uc.collection.id = :collectionId " +
            "and uc.accessStatus = :accessStatus")
    Optional<CollectionRole> getRoleInUserCollectionByAccessStatus(
            @Param("userId") Long userId,
            @Param("collectionId") Integer collectionId,
            @Param("accessStatus") AccessStatus accessStatus);

    default Optional<CollectionRole> getActiveRoleInUserCollection(Long userId, Integer collectionId) {
        return getRoleInUserCollectionByAccessStatus(userId, collectionId, AccessStatus.ENABLE);
    }

    @Query("select uc from UserCollection uc where uc.accessStatus = :accessStatus AND uc.collection.id = :collectionId")
    List<UserCollection> findUserByAccessStatus(@Param("accessStatus")AccessStatus accessStatus, @Param("collectionId") Integer collectionId);

    // Lấy Top 10 bộ thẻ mở gần đây nhất của User
    List<UserCollection> findTop10ByUserIdOrderByLastOpenedAtDesc(Long userId);

    // Tìm kiếm chính xác 1 record theo cặp ID
    Optional<UserCollection> findByUserIdAndCollectionId(Long userId, Integer collectionId);
}
