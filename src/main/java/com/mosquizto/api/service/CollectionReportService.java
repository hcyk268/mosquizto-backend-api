package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.util.CollectionReportStatus;

import java.util.List;

public interface CollectionReportService {
    CollectionReportResponse reportCollection(Integer collectionId, CollectionReportRequest request);
    // Lấy các report đang chờ xử lý của mình
    List<CollectionReportResponse> getMyPendingReports();

    // Xử lý report (khi user bấm vào thông báo đọc và xác nhận)
    void processReport(Long reportId, CollectionReportStatus status);
}
