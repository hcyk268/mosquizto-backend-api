package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionReportRequest;
import com.mosquizto.api.dto.request.UserReportRequest;
import com.mosquizto.api.dto.response.CollectionReportResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserReportResponse;
import com.mosquizto.api.service.CollectionReportService;
import com.mosquizto.api.service.UserReportService;
import com.mosquizto.api.util.CollectionReportStatus;
import com.mosquizto.api.util.UserReportStatus;
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
    private final UserReportService userReportService;

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

    @Operation(summary = "Report a user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/users/{username}")
    public ResponseData<UserReportResponse> reportUser(
            @PathVariable String username,
            @Valid @RequestBody UserReportRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Report user successfully",
                this.userReportService.reportUser(username, request));
    }

    @Operation(summary = "Get my pending user reports", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public ResponseData<List<UserReportResponse>> getMyPendingUserReports() {
        return new ResponseData<>(HttpStatus.OK.value(), "success",
                this.userReportService.getMyPendingReports());
    }

    @Operation(summary = "Dismiss user report", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/users/{reportId}")
    public ResponseData<Void> processUserReport(
            @PathVariable @NotNull @Positive Long reportId,
            @RequestParam("status") UserReportStatus status) {
        this.userReportService.processReport(reportId, status);
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }

    @Operation(summary = "Process collection report", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{reportId}")
    public  ResponseData<Void> processReport(@RequestParam("status")CollectionReportStatus  collectionReportStatus,
                                             @PathVariable("reportId") Long  reportId)
    {
        this.collectionReportService.processReport(reportId,collectionReportStatus);
        return new ResponseData<>(HttpStatus.OK.value(), "success") ;
    }
}
