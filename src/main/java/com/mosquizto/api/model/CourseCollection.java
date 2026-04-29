package com.mosquizto.api.model;

import com.mosquizto.api.util.AccessStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_course_collection")
public class CourseCollection extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "access_status", columnDefinition = "access_status")
    private AccessStatus accessStatus;

    public static CourseCollection create(Course course, Collection collection, Integer orderIndex) {
        return CourseCollection.builder()
                .course(course)
                .collection(collection)
                .orderIndex(orderIndex)
                .accessStatus(AccessStatus.ENABLE)
                .build();
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

    public void enable() {
        this.accessStatus = AccessStatus.ENABLE;
    }

    public void markPending() {
        this.accessStatus = AccessStatus.PENDING;
    }

    public void deny() {
        this.accessStatus = AccessStatus.DENIED;
    }

    public void updateOrder(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
