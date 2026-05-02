package com.mosquizto.api.repository;

import com.mosquizto.api.model.FolderCollection;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderCollectionRepository extends JpaRepository<FolderCollection, Long> {

    void deleteAllByFolderId(Long folderId);

    @Query("SELECT COALESCE(MAX(fc.orderIndex), 0) " +
            "FROM FolderCollection fc " +
            "WHERE fc.folder.id = :folderId")
    Integer findMaxOrderIndexCollection(@Param("folderId") Long folderId);

    Boolean existsByFolderIdAndCollectionId(Long folderId, Integer collectionId);

    Optional<FolderCollection> findByFolderIdAndCollectionId(Long folderId, Integer collectionId);

}
