package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.util.CollectionReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_collection_report")
public class CollectionReport extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CollectionReportStatus status;

    public static CollectionReport create(Collection collection, User reporter, String reason, String description) {
        validateCollectionAndReporter(collection, reporter);
        return CollectionReport.builder()
                .collection(collection)
                .reporter(reporter)
                .reason(reason)
                .description(description)
                .status(CollectionReportStatus.PENDING)
                .build();
    }

    public void updateContent(String reason, String description) {
        if (reason != null) {
            this.reason = reason;
        }

        if (description != null) {
            this.description = description;
        }

        this.status = CollectionReportStatus.PENDING;
    }

    private static void validateCollectionAndReporter(Collection collection, User reporter) {
        if (collection == null) {
            throw new InvalidDataException("Collection must not be null");
        }

        if (reporter == null) {
            throw new InvalidDataException("Reporter must not be null");
        }
    }
}
