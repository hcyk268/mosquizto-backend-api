package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserFolder;
import com.mosquizto.api.model.key.UserFolderId;
import com.mosquizto.api.util.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFolderRepository extends JpaRepository<UserFolder, UserFolderId> {

    @Query("select uf from UserFolder uf " +
            "where uf.user.id = :userId " +
            "and uf.folder.id = :folderId " +
            "and uf.deletedAt is null " +
            "and uf.user.deletedAt is null " +
            "and uf.folder.deletedAt is null")
    Optional<UserFolder> findActiveByUserIdAndFolderId(@Param("userId") Long userId, @Param("folderId") Long folderId);

    @Query("select uf from UserFolder uf join fetch uf.user " +
            "where uf.folder.id = :folderId " +
            "and uf.accessStatus = :accessStatus " +
            "and uf.deletedAt is null " +
            "and uf.user.deletedAt is null " +
            "and uf.folder.deletedAt is null")
    List<UserFolder> findActiveMembersByFolderIdAndStatus(
            @Param("folderId") Long folderId,
            @Param("accessStatus") AccessStatus accessStatus);

    default List<UserFolder> findAllActiveMembersByFolderId(Long folderId) {
        return findActiveMembersByFolderIdAndStatus(folderId, AccessStatus.ENABLE);
    }
}
