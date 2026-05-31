package com.mosquizto.api.repository;

import com.mosquizto.api.model.FolderCollection;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderCollectionRepository extends JpaRepository<FolderCollection, Long> {

    @Modifying
    @Query("DELETE FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId " +
            "AND fc.deletedAt IS NULL")
    void deleteAllActiveByFolderId(@Param("folderId") Long folderId);

    @Query("SELECT COALESCE(MAX(fc.orderIndex), 0) " +
            "FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId " +
            "AND fc.deletedAt IS NULL " +
            "AND fc.folder.deletedAt IS NULL " +
            "AND fc.collection.deletedAt IS NULL")
    Integer findMaxActiveOrderIndex(@Param("folderId") Long folderId);

    @Query("SELECT COUNT(fc) > 0 FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId " +
            "AND fc.collection.id = :collectionId " +
            "AND fc.deletedAt IS NULL " +
            "AND fc.folder.deletedAt IS NULL " +
            "AND fc.collection.deletedAt IS NULL")
    Boolean existsActiveByFolderIdAndCollectionId(
            @Param("folderId") Long folderId,
            @Param("collectionId") Integer collectionId);

    @Query("SELECT fc FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId " +
            "AND fc.collection.id = :collectionId " +
            "AND fc.deletedAt IS NULL " +
            "AND fc.folder.deletedAt IS NULL " +
            "AND fc.collection.deletedAt IS NULL")
    Optional<FolderCollection> findActiveByFolderIdAndCollectionId(
            @Param("folderId") Long folderId,
            @Param("collectionId") Integer collectionId);

    @Query("SELECT fc FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId " +
            "AND fc.collection.id = :collectionId")
    Optional<FolderCollection> findByFolderIdAndCollectionId(
            @Param("folderId") Long folderId,
            @Param("collectionId") Integer collectionId);

}
