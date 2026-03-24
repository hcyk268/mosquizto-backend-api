package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCollectionReposotory extends JpaRepository<UserCollection, UserCollectionId> {

    @Query("select uc from UserCollection uc join fetch uc.user where uc.collection.id = :collectionId")
    List<UserCollection> findAllMembersByCollectionId(@Param("collectionId") Integer collectionId);
}
