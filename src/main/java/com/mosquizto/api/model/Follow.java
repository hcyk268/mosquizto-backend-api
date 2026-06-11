package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_follow")
public class Follow extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Builder.Default
    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;

    public static Follow create(User follower, User following) {
        validateUsers(follower, following);

        return Follow.builder()
                .follower(follower)
                .following(following)
                .notificationsEnabled(true)
                .build();
    }

    public void unfollow() {
        this.setDeletedAt(new Date());
        this.setDeletedBy(this.follower);
    }

    public void restore() {
        this.setDeletedAt(null);
        this.setDeletedBy(null);
    }

    public boolean isActive() {
        return this.getDeletedAt() == null;
    }

    public boolean isFollowedBy(User user) {
        return hasSameId(this.follower, user);
    }

    public boolean targets(User user) {
        return hasSameId(this.following, user);
    }

    public boolean matches(User follower, User following) {
        return isFollowedBy(follower) && targets(following);
    }

    public void enableNotifications() {
        this.notificationsEnabled = true;
    }

    public void disableNotifications() {
        this.notificationsEnabled = false;
    }

    private static boolean hasSameId(User left, User right) {
        return left != null
                && right != null
                && left.getId() != null
                && left.getId().equals(right.getId());
    }

    private static void validateUsers(User follower, User following) {
        if (follower == null) {
            throw new InvalidDataException("Follower must not be null");
        }

        if (following == null) {
            throw new InvalidDataException("Following must not be null");
        }

        if (follower.getId() == null || following.getId() == null) {
            throw new InvalidDataException("Follower and following must be persisted users");
        }

        if (follower.getId().equals(following.getId())) {
            throw new InvalidDataException("You can not follow yourself");
        }
    }

}
