package com.mosquizto.api.repository;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import org.hibernate.Internal;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection, UserCollectionId> {

    @Query("select uc from UserCollection uc join fetch uc.user " +
            "where uc.collection.id = :collectionId and uc.accessStatus = 'ENABLE'")
    List<UserCollection> findAllActiveMembersByCollectionId(@Param("collectionId") Integer collectionId);

    // Lấy role nhưng phải kèm điều kiện đã ENABLE
    @Query("select uc.role from UserCollection uc " +
            "where uc.user.id = :userId and uc.collection.id = :collectionId and uc.accessStatus = 'ENABLE'")
    Optional<CollectionRole> getActiveRoleInUserCollection(@Param("userId") Long userId, @Param("collectionId") Integer collectionId);

    @Query("select uc from UserCollection uc where uc.accessStatus = :accessStatus AND uc.collection.id = :collectionId")
    List<UserCollection> findUserByAccessStatus(@Param("accessStatus")AccessStatus accessStatus, @Param("collectionId") Integer collectionId);
}
