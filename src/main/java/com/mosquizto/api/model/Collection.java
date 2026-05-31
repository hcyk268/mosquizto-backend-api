package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_collection")
@Entity
@Builder
@Getter
@Setter
public class Collection extends AbstractEntity<Integer> {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description ;

    private Boolean visibility ;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudySession> studySessions = new ArrayList<>();

    @ColumnDefault("0")
    private Integer count;

    public static Collection initialize(User owner, String title, String description, Boolean visibility) {
        if (owner == null) {
            throw new InvalidDataException("Collection owner must not be null");
        }

        return Collection.builder()
                .createdBy(owner)
                .title(title)
                .description(description)
                .visibility(visibility)
                .count(0)
                .build();
    }

    public boolean isPublic() {
        return Boolean.TRUE.equals(this.visibility);
    }

    public boolean isOwnedBy(User user) {
        return this.createdBy != null
                && this.createdBy.getId() != null
                && user != null
                && user.getId() != null
                && this.createdBy.getId().equals(user.getId());
    }

    public boolean isOwnedBy(String username) {
        return this.createdBy != null
                && this.createdBy.getUsername() != null
                && this.createdBy.getUsername().equals(username);
    }

    public boolean canView(User user, UserCollection membership) {
        return isPublic() || isOwnedBy(user) || (membership != null && membership.canView());
    }

    public boolean canEdit(UserCollection membership) {
        return membership != null && membership.canEdit();
    }

    public boolean canDelete(UserCollection membership) {
        return membership != null && membership.isOwner() && membership.isActive();
    }

    public void increaseItemCount() {
        this.count = safeCount() + 1;
    }

    public void decreaseItemCount() {
        int nextCount = safeCount() - 1;
        this.count = Math.max(nextCount, 0);
    }

    public void updateInfo(String title, String description, Boolean visibility) {
        if (title != null) {
            this.title = title;
        }

        if (description != null) {
            this.description = description;
        }

        if (visibility != null) {
            this.visibility = visibility;
        }
    }

    public void delete(User deleteBy) {
        this.setDeletedAt(new Date());
        this.setDeletedBy(deleteBy);
    }

    public int getItemCount() {
        return safeCount();
    }

    private int safeCount() {
        return this.count == null ? 0 : this.count;
    }
}

