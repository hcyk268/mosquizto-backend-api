package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;

public interface CollectionReportService {
    CollectionReportResponse reportCollection(Integer collectionId, CollectionReportRequest request);
}
