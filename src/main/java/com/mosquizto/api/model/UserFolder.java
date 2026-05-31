package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.model.key.UserFolderId;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.FolderRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_folder")
public class UserFolder {

    @EmbeddedId
    private UserFolderId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("folderId")
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "folder_role")
    private FolderRole role;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", columnDefinition = "access_status")
    private AccessStatus accessStatus;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    public static UserFolder createOwner(User user, Folder folder) {
        validateUserAndFolder(user, folder);
        return UserFolder.builder()
                .id(buildId(user, folder))
                .user(user)
                .folder(folder)
                .role(FolderRole.OWNER)
                .accessStatus(AccessStatus.ENABLE)
                .build();
    }

    public static UserFolder createShared(User user, Folder folder, FolderRole role) {
        validateUserAndFolder(user, folder);
        validateShareRole(role);
        return UserFolder.builder()
                .id(buildId(user, folder))
                .user(user)
                .folder(folder)
                .role(role)
                .accessStatus(AccessStatus.ENABLE)
                .build();
    }

    public void changeRole(FolderRole role) {
        if (role == null) {
            return;
        }

        validateShareRole(role);

        if (isOwner()) {
            throw new InvalidDataException("Owner role cannot be changed");
        }

        this.role = role;
    }

    public void enable() {
        this.accessStatus = AccessStatus.ENABLE;
    }

    public boolean isOwner() {
        return FolderRole.OWNER.equals(this.role);
    }

    public boolean canView() {
        return AccessStatus.ENABLE.equals(this.accessStatus);
    }

    public boolean canManage() {
        return canView() && (FolderRole.OWNER.equals(this.role) || FolderRole.EDITOR.equals(this.role));
    }

    private static void validateUserAndFolder(User user, Folder folder) {
        if (user == null) {
            throw new InvalidDataException("User must not be null");
        }

        if (folder == null) {
            throw new InvalidDataException("Folder must not be null");
        }
    }

    private static void validateShareRole(FolderRole role) {
        if (role == null) {
            throw new InvalidDataException("Folder role must not be null");
        }

        if (FolderRole.OWNER.equals(role)) {
            throw new InvalidDataException("Role OWNER is reserved for the folder creator");
        }
    }

    private static UserFolderId buildId(User user, Folder folder) {
        return UserFolderId.builder()
                .userId(user.getId())
                .folderId(folder.getId())
                .build();
    }
}
