package com.mosquizto.api.model;


import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "tbl_user_collection")
public class UserCollection {

    @EmbeddedId
    private UserCollectionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "collection_role")
    private CollectionRole role;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "access_status", columnDefinition = "access_status")
    private AccessStatus accessStatus ;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name="last_opened_at")
    private Date lastOpenedAt ;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    public static UserCollection createOwner(User user, Collection collection) {
        validateUserAndCollection(user, collection);

        return UserCollection.builder()
                .id(buildId(user, collection))
                .user(user)
                .collection(collection)
                .role(CollectionRole.OWNER)
                .accessStatus(AccessStatus.ENABLE)
                .build();
    }

    public static UserCollection createShareInvite(User user, Collection collection, CollectionRole role , User inviter) {
        validateUserAndCollection(user, collection);
        validateShareRole(role);

        return UserCollection.builder()
                .id(buildId(user, collection))
                .user(user)
                .collection(collection)
                .role(role)
                .accessStatus(AccessStatus.PENDING)
                .invitedBy(inviter)
                .build();
    }

    public static UserCollection requestJoin(User user, Collection collection) {
        validateUserAndCollection(user, collection);

        if (!collection.isPublic()) {
            throw new AccessDeniedException("Collection is private");
        }

        if (collection.isOwnedBy(user)) {
            throw new ConflictException("Owner does not need to join their own collection");
        }

        return UserCollection.builder()
                .id(buildId(user, collection))
                .user(user)
                .collection(collection)
                .role(CollectionRole.VIEWER)
                .accessStatus(AccessStatus.PENDING)
                .lastOpenedAt(new Date())
                .build();
    }

    public void approve() {
        this.accessStatus = AccessStatus.ENABLE;
    }

    public void deny() {
        this.accessStatus = AccessStatus.DENIED;
    }

    public void markPending() {
        this.accessStatus = AccessStatus.PENDING;
    }

    public void changeRole(CollectionRole role) {
        if (role == null) {
            return;
        }

        if (CollectionRole.OWNER.equals(role) && !isOwner()) {
            throw new InvalidDataException("Role OWNER is reserved for the collection creator");
        }

        if (isOwner() && !CollectionRole.OWNER.equals(role)) {
            throw new InvalidDataException("Owner role cannot be changed");
        }

        this.role = role;
    }

    public void touchLastOpenedAt(Date date) {
        this.lastOpenedAt = date != null ? date : new Date();
    }

    public void delete(User deleteBy) {
        this.deletedAt = new Date();
        this.deletedBy = deleteBy;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }

    public boolean isOwner() {
        return CollectionRole.OWNER.equals(this.role);
    }

    public boolean canEdit() {
        return isActive() && (CollectionRole.OWNER.equals(this.role) || CollectionRole.EDITOR.equals(this.role));
    }

    public boolean canView() {
        return isActive();
    }

    public boolean canViewContent() {
        return isActive() || isPending();
    }

    public boolean isActive() {
        return AccessStatus.ENABLE.equals(this.accessStatus);
    }

    public boolean isPending() {
        return AccessStatus.PENDING.equals(this.accessStatus);
    }

    public boolean isDenied() {
        return AccessStatus.DENIED.equals(this.accessStatus);
    }

    public void inviteBy(User invitedBy) {
        this.invitedBy = invitedBy;
    }

    private static void validateUserAndCollection(User user, Collection collection) {
        if (user == null) {
            throw new InvalidDataException("User must not be null");
        }

        if (collection == null) {
            throw new InvalidDataException("Collection must not be null");
        }
    }

    private static void validateShareRole(CollectionRole role) {
        if (role == null) {
            throw new InvalidDataException("Collection role must not be null");
        }

        if (CollectionRole.OWNER.equals(role)) {
            throw new InvalidDataException("Role OWNER is reserved for the collection creator");
        }
    }

    private static UserCollectionId buildId(User user, Collection collection) {
        return UserCollectionId.builder()
                .userId(user.getId())
                .collectionId(collection.getId())
                .build();
    }
}
