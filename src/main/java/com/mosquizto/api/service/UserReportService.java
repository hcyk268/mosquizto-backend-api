package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.UserReportRequest;
import com.mosquizto.api.dto.response.UserReportResponse;
import com.mosquizto.api.util.UserReportStatus;

import java.util.List;

public interface UserReportService {

    UserReportResponse reportUser(String username, UserReportRequest request);

    List<UserReportResponse> getMyPendingReports();

    void processReport(Long reportId, UserReportStatus status);
}
