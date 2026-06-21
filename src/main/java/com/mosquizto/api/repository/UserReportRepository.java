package com.mosquizto.api.repository;

import com.mosquizto.api.model.UserReport;
import com.mosquizto.api.util.UserReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    @Query("SELECT report FROM UserReport report " +
            "WHERE report.reporter.id = :reporterId " +
            "AND report.reportedUser.id = :reportedUserId " +
            "AND report.deletedAt IS NULL")
    Optional<UserReport> findActiveByReporterAndReportedUser(@Param("reporterId") Long reporterId,
                                                           @Param("reportedUserId") Long reportedUserId);

    @Query("SELECT report FROM UserReport report " +
            "WHERE report.reportedUser.id = :reportedUserId " +
            "AND report.status = :status " +
            "AND report.deletedAt IS NULL " +
            "ORDER BY report.createdAt DESC")
    List<UserReport> findByReportedUserAndStatus(@Param("reportedUserId") Long reportedUserId,
                                                 @Param("status") UserReportStatus status);
}
