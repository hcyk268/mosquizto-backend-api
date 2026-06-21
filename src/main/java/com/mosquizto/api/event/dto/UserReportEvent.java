package com.mosquizto.api.event.dto;

public record UserReportEvent(
        Long reportId,
        String targetUsername
) {}
