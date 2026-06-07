package com.mosquizto.api.model;

import com.mosquizto.api.util.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_notification")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, columnDefinition = "notification_type")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NotificationType type; // COLLECTION_SHARED, COLLECTION_REPORTED, ...

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "read_at")
    @UpdateTimestamp
    private LocalDateTime readAt;

    // Metadata tuỳ loại thông báo (collectionId, reportId, ...)
    @Column(name = "reference_id")
    private Long referenceId;
}