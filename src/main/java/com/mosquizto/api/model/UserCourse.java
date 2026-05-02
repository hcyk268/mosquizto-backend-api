package com.mosquizto.api.model;

import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_course")
public class UserCourse extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "course_role")
    private CourseRole role;

    @Column(name = "joined_at")
    @CreationTimestamp
    private Date joinedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "access_status", columnDefinition = "access_status")
    private AccessStatus accessStatus;

    public static UserCourse create(User user, Course course, CourseRole role, AccessStatus accessStatus) {
        return UserCourse.builder()
                .user(user)
                .course(course)
                .role(role != null ? role : CourseRole.STUDENT)
                .accessStatus(accessStatus != null ? accessStatus : AccessStatus.ENABLE)
                .build();
    }

    public boolean isTeacher() {
        return CourseRole.TEACHER.equals(this.role);
    }

    public boolean isStudent() {
        return CourseRole.STUDENT.equals(this.role);
    }

    public boolean isEnabled() {
        return AccessStatus.ENABLE.equals(this.accessStatus);
    }

    public boolean isPending() {
        return AccessStatus.PENDING.equals(this.accessStatus);
    }

    public boolean isDenied() {
        return AccessStatus.DENIED.equals(this.accessStatus);
    }

    public void approve() {
        this.accessStatus = AccessStatus.ENABLE;
    }

    public void markPending() {
        this.accessStatus = AccessStatus.PENDING;
    }

    public void deny() {
        this.accessStatus = AccessStatus.DENIED;
    }

    public void changeRole(CourseRole role) {
        if (role != null) {
            this.role = role;
        }
    }

    public void changeAccessStatus(AccessStatus accessStatus) {
        if (accessStatus != null) {
            this.accessStatus = accessStatus;
        }
    }
}
