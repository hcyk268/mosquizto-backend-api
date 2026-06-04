package com.mosquizto.api.repository;

import com.mosquizto.api.model.CollectionReport;
import com.mosquizto.api.util.CollectionReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionReportRepository extends JpaRepository<CollectionReport, Long> {

    @Query("select report from CollectionReport report " +
            "where report.collection.id = :collectionId " +
            "and report.reporter.id = :reporterId " +
            "and report.deletedAt is null " +
            "and report.collection.deletedAt is null " +
            "and report.reporter.deletedAt is null")
    Optional<CollectionReport> findActiveByCollectionIdAndReporterId(
            @Param("collectionId") Integer collectionId,
            @Param("reporterId") Long reporterId);

    @Query("select r from CollectionReport r join fetch r.collection c " +
            "where c.createdBy.id = :ownerId " +
            "and r.status = :status " +
            "and c.deletedAt is null")
    List<CollectionReport> findReportsByCollectionOwnerAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") CollectionReportStatus status);
}
