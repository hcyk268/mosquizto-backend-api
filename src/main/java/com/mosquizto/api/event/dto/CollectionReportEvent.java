package com.mosquizto.api.event.dto;

public record CollectionReportEvent (
        Long reportId,
        Long reporterId,
        String targetUsername,
        String targetMail,
        String reporterName,
        String reporterMail,
        String collectionTitle,
        String reason,
        String description
) {}
