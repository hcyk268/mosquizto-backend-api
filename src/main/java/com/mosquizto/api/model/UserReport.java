package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.util.UserReportStatus;
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
@Table(name = "tbl_user_report")
public class UserReport extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserReportStatus status;

    public static UserReport create(User reporter, User reportedUser, String reason, String description) {
        validateUsers(reporter, reportedUser);

        return UserReport.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(reason)
                .description(description)
                .status(UserReportStatus.PENDING)
                .build();
    }

    public void updateContent(String reason, String description) {
        if (reason != null) {
            this.reason = reason;
        }

        if (description != null) {
            this.description = description;
        }

        this.status = UserReportStatus.PENDING;
    }

    public void dismiss() {
        this.status = UserReportStatus.DISMISSED;
    }

    private static void validateUsers(User reporter, User reportedUser) {
        if (reporter == null || reportedUser == null) {
            throw new InvalidDataException("Reporter and reported user must not be null");
        }

        if (reporter.getId() == null || reportedUser.getId() == null) {
            throw new InvalidDataException("Reporter and reported user must be persisted");
        }

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new InvalidDataException("You cannot report yourself");
        }
    }
}
