package com.mosquizto.api.event.dto;

public record CollectionReportEvent (
        Long reportId ,
        String targetUsername ,
        String targetMail,
        String reporterName ,
        String reporterMail,
        String collectionTitle,
        String reason ,
        String description
){}
