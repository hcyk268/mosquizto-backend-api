package com.mosquizto.api.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
