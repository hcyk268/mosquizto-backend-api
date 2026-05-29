package com.mosquizto.api.repository;

import com.mosquizto.api.model.CollectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionReportRepository extends JpaRepository<CollectionReport, Long> {

    @Query("select report from CollectionReport report " +
            "where report.collection.id = :collectionId " +
            "and report.reporter.id = :reporterId")
    Optional<CollectionReport> findByCollectionIdAndReporterId(
            @Param("collectionId") Integer collectionId,
            @Param("reporterId") Long reporterId);
}
