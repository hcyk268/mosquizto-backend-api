package com.mosquizto.api.controller.reports;

import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionReportService;
import com.mosquizto.api.service.UserReportService;
import com.mosquizto.api.util.CollectionReportStatus;
import com.mosquizto.api.util.UserReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Moderation Reports", description = "APIs for processing submitted reports")
public class ModerationReportsController {

    private final CollectionReportService collectionReportService;
    private final UserReportService userReportService;

    @Operation(summary = "Dismiss user report", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/reports/users/{reportId}")
    public ResponseData<Void> processUserReport(@PathVariable @NotNull @Positive Long reportId,
                                                @RequestParam("status") UserReportStatus status) {
        this.userReportService.processReport(reportId, status);
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }

    @Operation(summary = "Process collection report", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/reports/collections/{reportId}")
    public ResponseData<Void> processCollectionReport(@RequestParam("status") CollectionReportStatus collectionReportStatus,
                                                      @PathVariable("reportId") Long reportId) {
        this.collectionReportService.processReport(reportId, collectionReportStatus);
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }
}
