package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionReportService;
import com.mosquizto.api.util.CollectionReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "APIs for reporting content")
public class ReportController {

    private final CollectionReportService collectionReportService;

    @Operation(summary = "Report a collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections/{collectionId}")
    public ResponseData<CollectionReportResponse> reportCollection(
            @PathVariable @NotNull @Positive Integer collectionId,
            @Valid @RequestBody CollectionReportRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Report collection successfully",
                this.collectionReportService.reportCollection(collectionId, request));
    }

    @Operation(summary = "Get my pending reports", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections")
    public ResponseData<List<CollectionReportResponse>> getMyPendingReports()
    {
        return new ResponseData<>(HttpStatus.OK.value(), "success",this.collectionReportService.getMyPendingReports()) ;
    }
    @Operation(summary = "Process report", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{reportId}")
    public  ResponseData<Void> processReport(@RequestParam("status")CollectionReportStatus  collectionReportStatus,
                                             @PathVariable("reportId") Long  reportId)
    {
        this.collectionReportService.processReport(reportId,collectionReportStatus);
        return new ResponseData<>(HttpStatus.OK.value(), "success") ;
    }
}
