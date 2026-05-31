package com.mosquizto.api.repository;

import com.mosquizto.api.model.Folder;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT f FROM Folder f " +
            "WHERE f.createdBy.id = :userId " +
            "AND f.deletedAt IS NULL " +
            "AND f.createdBy.deletedAt IS NULL " +
            "ORDER BY f.createdAt DESC")
    List<Folder> findAllActiveByCreatorId(@Param("userId") Long userId);

    @Query("SELECT f FROM Folder f " +
            "WHERE f.id = :folderId " +
            "AND f.deletedAt IS NULL")
    Optional<Folder> findActiveById(@Param("folderId") Long folderId);

    @Query("SELECT f FROM Folder f " +
            "WHERE f.id = :folderId " +
            "AND f.createdBy.id = :userId " +
            "AND f.deletedAt IS NULL " +
            "AND f.createdBy.deletedAt IS NULL")
    Optional<Folder> findActiveByIdAndCreatorId(
            @Param("folderId") Long folderId,
            @Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "JOIN FETCH f.createdBy " +
            "LEFT JOIN FETCH f.folderCollections fc " +
            "LEFT JOIN FETCH fc.collection " +
            "WHERE f.id = :folderId " +
            "AND f.createdBy.id = :userId " +
            "AND f.deletedAt IS NULL " +
            "AND f.createdBy.deletedAt IS NULL " +
            "AND (fc IS NULL OR fc.deletedAt IS NULL) " +
            "AND (fc.collection IS NULL OR fc.collection.deletedAt IS NULL)")
    Optional<Folder> findActiveByIdAndCreatorIdWithCollections(
            @Param("folderId") Long folderId,
            @Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "LEFT JOIN UserFolder uf ON uf.folder.id = f.id " +
            "AND uf.user.id = :userId " +
            "AND uf.accessStatus = :accessStatus " +
            "AND uf.deletedAt IS NULL " +
            "WHERE (f.createdBy.id = :userId OR uf.user.id = :userId) " +
            "AND f.deletedAt IS NULL " +
            "AND f.createdBy.deletedAt IS NULL " +
            "ORDER BY f.createdAt DESC")
    List<Folder> findAccessibleActiveByUserId(
            @Param("userId") Long userId,
            @Param("accessStatus") AccessStatus accessStatus);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "JOIN FETCH f.createdBy " +
            "LEFT JOIN FETCH f.folderCollections fc " +
            "LEFT JOIN FETCH fc.collection " +
            "WHERE f.id = :folderId " +
            "AND f.deletedAt IS NULL " +
            "AND f.createdBy.deletedAt IS NULL " +
            "AND (fc IS NULL OR fc.deletedAt IS NULL) " +
            "AND (fc.collection IS NULL OR fc.collection.deletedAt IS NULL)")
    Optional<Folder> findActiveByIdWithCollections(@Param("folderId") Long folderId);
}
