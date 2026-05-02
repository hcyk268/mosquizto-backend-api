package com.mosquizto.api.model;

import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
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
@Table(name = "tbl_course")
public class Course extends AbstractEntity<Long> {

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "visibility")
    private Boolean visibility;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseCollection> courseCollections = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserCourse> userCourses = new ArrayList<>();

    public boolean isVisible() {
        return Boolean.TRUE.equals(this.visibility);
    }

    public CourseCollection addCollection(Collection collection, Integer orderIndex) {
        if (this.courseCollections == null) {
            this.courseCollections = new ArrayList<>();
        }

        CourseCollection existing = findCourseCollection(collection);
        if (existing != null) {
            existing.updateOrder(orderIndex);
            existing.enable();
            return existing;
        }

        CourseCollection courseCollection = CourseCollection.create(this, collection, orderIndex);
        this.courseCollections.add(courseCollection);
        return courseCollection;
    }

    public void removeCollection(Collection collection) {
        if (this.courseCollections == null) {
            return;
        }

        this.courseCollections.removeIf(courseCollection ->
                sameCollection(courseCollection.getCollection(), collection));
    }

    public UserCourse addMember(User user, CourseRole role, AccessStatus accessStatus) {
        if (this.userCourses == null) {
            this.userCourses = new ArrayList<>();
        }

        UserCourse existing = findUserCourse(user);
        if (existing != null) {
            existing.changeRole(role);
            existing.changeAccessStatus(accessStatus);
            return existing;
        }

        UserCourse userCourse = UserCourse.create(user, this, role, accessStatus);
        this.userCourses.add(userCourse);
        return userCourse;
    }

    public void removeMember(User user) {
        if (this.userCourses == null) {
            return;
        }

        this.userCourses.removeIf(userCourse -> sameUser(userCourse.getUser(), user));
    }

    public boolean hasEnabledMember(User user) {
        UserCourse userCourse = findUserCourse(user);
        return userCourse != null && userCourse.isEnabled();
    }

    public boolean hasTeacher(User user) {
        UserCourse userCourse = findUserCourse(user);
        return userCourse != null && userCourse.isTeacher() && userCourse.isEnabled();
    }

    private CourseCollection findCourseCollection(Collection collection) {
        if (collection == null || this.courseCollections == null) {
            return null;
        }

        return this.courseCollections.stream()
                .filter(courseCollection -> sameCollection(courseCollection.getCollection(), collection))
                .findFirst()
                .orElse(null);
    }

    private UserCourse findUserCourse(User user) {
        if (user == null || this.userCourses == null) {
            return null;
        }

        return this.userCourses.stream()
                .filter(userCourse -> sameUser(userCourse.getUser(), user))
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

    private boolean sameUser(User left, User right) {
        if (left == null || right == null) {
            return false;
        }

        if (left.getId() != null && right.getId() != null) {
            return Objects.equals(left.getId(), right.getId());
        }

        return left == right;
    }
}
