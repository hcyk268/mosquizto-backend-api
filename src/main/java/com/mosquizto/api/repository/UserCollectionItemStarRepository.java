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

    long countByUserId(Long userId);

    @Query("select star from UserCollectionItemStar star " +
            "where star.user.id = :userId " +
            "and star.collectionItem.id = :collectionItemId")
    Optional<UserCollectionItemStar> findByUserIdAndCollectionItemId(
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
            "and star.collectionItem.id = :collectionItemId")
    boolean existsByUserIdAndCollectionItemId(
            @Param("userId") Long userId,
            @Param("collectionItemId") Integer collectionItemId);

    @Query("select star from UserCollectionItemStar star " +
            "join fetch star.collectionItem item " +
            "join fetch item.collection " +
            "where star.user.id = :userId " +
            "order by star.createdAt desc")
    List<UserCollectionItemStar> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("select star from UserCollectionItemStar star " +
            "join fetch star.collectionItem item " +
            "join fetch item.collection " +
            "where star.user.id = :userId " +
            "order by star.createdAt desc")
    List<UserCollectionItemStar> findRecentByUserId(
            @Param("userId") Long userId,
            Pageable pageable);
}
