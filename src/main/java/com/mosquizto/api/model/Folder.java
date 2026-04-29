package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_folder")
public class Folder extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FolderCollection> folderCollections = new ArrayList<>();

    public boolean isOwnedBy(String username) {
        return this.createdBy != null
                && this.createdBy.getUsername() != null
                && this.createdBy.getUsername().equals(username);
    }

    public FolderCollection addCollection(Collection collection, Integer orderIndex) {
        if (this.folderCollections == null) {
            this.folderCollections = new ArrayList<>();
        }

        FolderCollection existing = findFolderCollection(collection);
        if (existing != null) {
            existing.updateOrder(orderIndex);
            return existing;
        }

        FolderCollection folderCollection = FolderCollection.create(this, collection, orderIndex);
        this.folderCollections.add(folderCollection);
        return folderCollection;
    }

    public void removeCollection(Collection collection) {
        if (this.folderCollections == null) {
            return;
        }

        this.folderCollections.removeIf(folderCollection ->
                sameCollection(folderCollection.getCollection(), collection));
    }

    public boolean containsCollection(Collection collection) {
        return findFolderCollection(collection) != null;
    }

    private FolderCollection findFolderCollection(Collection collection) {
        if (collection == null || this.folderCollections == null) {
            return null;
        }

        return this.folderCollections.stream()
                .filter(folderCollection -> sameCollection(folderCollection.getCollection(), collection))
                .findFirst()
                .orElse(null);
    }

    private boolean sameCollection(Collection left, Collection right) {
        if (left == null || right == null) {
            return false;
        }

        if (left.getId() != null && right.getId() != null) {
            return Objects.equals(left.getId(), right.getId());
        }

        return left == right;
    }
}
