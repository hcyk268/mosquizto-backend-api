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

    List<Folder> findAllByCreatedByIdOrderByCreatedAtDesc(Long userId);

    Optional<Folder> findByIdAndCreatedById(Long folderId, Long userId);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "JOIN FETCH f.createdBy " +
            "LEFT JOIN FETCH f.folderCollections fc " +
            "LEFT JOIN FETCH fc.collection " +
            "WHERE f.id = :folderId AND f.createdBy.id = :userId")
    Optional<Folder> findByIdAndCreatedByIdWithCollections(@Param("folderId") Long folderId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "LEFT JOIN UserFolder uf ON uf.folder.id = f.id " +
            "AND uf.user.id = :userId " +
            "AND uf.accessStatus = :accessStatus " +
            "WHERE f.createdBy.id = :userId OR uf.user.id = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Folder> findAllAccessibleByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("accessStatus") AccessStatus accessStatus);

    @Query("SELECT DISTINCT f FROM Folder f " +
            "JOIN FETCH f.createdBy " +
            "LEFT JOIN FETCH f.folderCollections fc " +
            "LEFT JOIN FETCH fc.collection " +
            "WHERE f.id = :folderId")
    Optional<Folder> findByIdWithCollections(@Param("folderId") Long folderId);
}
