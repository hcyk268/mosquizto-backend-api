package com.mosquizto.api.event.dto;

public record UserReportEvent(
        Long reporterId,
        String targetUsername
) {}
