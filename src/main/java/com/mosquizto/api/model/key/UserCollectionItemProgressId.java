package com.mosquizto.api.model.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class UserCollectionItemProgressId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "collection_item_id")
    private Integer collectionItemId;

}
