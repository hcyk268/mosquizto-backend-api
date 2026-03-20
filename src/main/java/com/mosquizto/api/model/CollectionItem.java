package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_collection_item")
public class CollectionItem extends AbstractEntity<Integer> {
    @Column(name = "term")
    private String term;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(targetEntity = Collection.class , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private Collection collection ;

}
