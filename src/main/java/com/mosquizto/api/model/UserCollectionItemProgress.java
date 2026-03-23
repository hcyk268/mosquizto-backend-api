package com.mosquizto.api.model;

import com.mosquizto.api.model.key.UserCollectionItemProgressId;
import com.mosquizto.api.util.ProgressStatus;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user_collection_item_progress")
public class UserCollectionItemProgress {

    @EmbeddedId
    private UserCollectionItemProgressId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("collectionItemId")
    @JoinColumn(name = "collection_item_id")
    private CollectionItem collectionItem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private ProgressStatus status;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "wrong_count")
    private Integer wrongCount;

    @Column(name = "last_studied_at")
    private Date lastStudiedAt;

    @Column(name = "next_review_at")
    private Date nextReviewAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

}
