package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.model.key.UserCollectionItemStarId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_collection_item_star")
public class UserCollectionItemStar {

    @EmbeddedId
    private UserCollectionItemStarId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collectionItemId")
    @JoinColumn(name = "collection_item_id")
    private CollectionItem collectionItem;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    public static UserCollectionItemStar create(User user, CollectionItem item) {
        if (user == null) {
            throw new InvalidDataException("User must not be null");
        }

        if (item == null) {
            throw new InvalidDataException("Collection item must not be null");
        }

        return UserCollectionItemStar.builder()
                .id(UserCollectionItemStarId.builder()
                        .userId(user.getId())
                        .collectionItemId(item.getId())
                        .build())
                .user(user)
                .collectionItem(item)
                .build();
    }
}
