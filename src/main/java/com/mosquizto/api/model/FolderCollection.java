package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_folder_collection")
public class FolderCollection extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "order_index")
    private Integer orderIndex;

    public static FolderCollection create(Folder folder, Collection collection, Integer orderIndex) {
        return FolderCollection.builder()
                .folder(folder)
                .collection(collection)
                .orderIndex(orderIndex)
                .build();
    }

    public void updateOrder(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
