package com.mosquizto.api.model;


import com.mosquizto.api.model.key.UserCollectionId;
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

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name="last_opened_at")
    private Date lastOpenedAt ;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

}


