package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserCollectionItemStar;
import com.mosquizto.api.model.key.UserCollectionItemStarId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCollectionItemStarRepository extends JpaRepository<UserCollectionItemStar, UserCollectionItemStarId> {

    @Query("select count(star) from UserCollectionItemStar star " +
            "where star.user.id = :userId " +
            "and star.deletedAt is null " +
            "and star.user.deletedAt is null")
    long countActiveByUserId(@Param("userId") Long userId);

    @Query("select star from UserCollectionItemStar star " +
            "where star.user.id = :userId " +
            "and star.collectionItem.id = :collectionItemId " +
            "and star.deletedAt is null " +
            "and star.user.deletedAt is null " +
            "and star.collectionItem.deletedAt is null " +
            "and star.collectionItem.collection.deletedAt is null")
    Optional<UserCollectionItemStar> findActiveByUserIdAndCollectionItemId(
            @Param("userId") Long userId,
            @Param("collectionItemId") Integer collectionItemId);

    @Modifying
    @Query("delete from UserCollectionItemStar star " +
            "where star.user.id = :userId " +
            "and star.collectionItem.id = :collectionItemId")
    void deleteByUserIdAndCollectionItemId(
            @Param("userId") Long userId,
            @Param("collectionItemId") Integer collectionItemId);

    @Query("select count(star) > 0 from UserCollectionItemStar star " +
            "where star.user.id = :userId " +
            "and star.collectionItem.id = :collectionItemId " +
            "and star.deletedAt is null " +
            "and star.user.deletedAt is null " +
            "and star.collectionItem.deletedAt is null " +
            "and star.collectionItem.collection.deletedAt is null")
    boolean existsActiveByUserIdAndCollectionItemId(
            @Param("userId") Long userId,
            @Param("collectionItemId") Integer collectionItemId);

    @Query("select star from UserCollectionItemStar star " +
            "join fetch star.collectionItem item " +
            "join fetch item.collection collection " +
            "where star.user.id = :userId " +
            "and star.deletedAt is null " +
            "and star.user.deletedAt is null " +
            "and item.deletedAt is null " +
            "and collection.deletedAt is null " +
            "order by star.createdAt desc")
    List<UserCollectionItemStar> findAllActiveByUserId(@Param("userId") Long userId);

    @Query("select star from UserCollectionItemStar star " +
            "join fetch star.collectionItem item " +
            "join fetch item.collection collection " +
            "where star.user.id = :userId " +
            "and star.deletedAt is null " +
            "and star.user.deletedAt is null " +
            "and item.deletedAt is null " +
            "and collection.deletedAt is null " +
            "order by star.createdAt desc")
    List<UserCollectionItemStar> findRecentActiveByUserId(
            @Param("userId") Long userId,
            Pageable pageable);
}
