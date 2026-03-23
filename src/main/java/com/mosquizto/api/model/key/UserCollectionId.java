package com.mosquizto.api.model.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class UserCollectionId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "collection_id")
    private Integer collectionId;

}
