package com.mosquizto.api.model;


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

    public static UserCollection createShareInvite(User user, Collection collection, CollectionRole role) {
        validateUserAndCollection(user, collection);
        validateShareRole(role);

        return UserCollection.builder()
                .id(buildId(user, collection))
                .user(user)
                .collection(collection)
                .role(role)
                .accessStatus(AccessStatus.PENDING)
                .build();
    }

    public static UserCollection requestJoin(User user, Collection collection) {
        validateUserAndCollection(user, collection);

        if (!collection.isPublic()) {
            throw new InvalidDataException("Collection is private");
        }

        if (collection.getCreatedBy() != null && collection.getCreatedBy().getId() != null
                && collection.getCreatedBy().getId().equals(user.getId())) {
            throw new InvalidDataException("Owner does not need to join their own collection");
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

    public boolean isOwner() {
        return CollectionRole.OWNER.equals(this.role);
    }

    public boolean canEdit() {
        return isActive() && (CollectionRole.OWNER.equals(this.role) || CollectionRole.EDITOR.equals(this.role));
    }

    public boolean canView() {
        return isActive();
    }

    public boolean isActive() {
        return AccessStatus.ENABLE.equals(this.accessStatus);
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


