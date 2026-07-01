package com.mosquizto.api.controller.collections;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Collection Reports", description = "APIs for reporting collections")
public class CollectionReportsController {

    private final CollectionReportService collectionReportService;

    @Operation(summary = "Report a collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections/{collectionId}/reports")
    public ResponseData<CollectionReportResponse> reportCollection(
            @PathVariable @NotNull @Positive Integer collectionId,
            @Valid @RequestBody CollectionReportRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Report collection successfully",
                this.collectionReportService.reportCollection(collectionId, request));
    }

    @Operation(summary = "Get my pending collection reports", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/reports/collections")
    public ResponseData<List<CollectionReportResponse>> getMyPendingReports() {
        return new ResponseData<>(HttpStatus.OK.value(), "success",
                this.collectionReportService.getMyPendingReports());
    }
}
