package com.mosquizto.api.controller.reports;

import com.mosquizto.api.dto.request.UserReportRequest;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserReportResponse;
import com.mosquizto.api.service.UserReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "User Reports", description = "APIs for reporting users")
public class UserReportsController {

    private final UserReportService userReportService;

    @Operation(summary = "Report a user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/users/{username}/reports")
    public ResponseData<UserReportResponse> reportUser(@PathVariable String username,
                                                       @Valid @RequestBody UserReportRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Report user successfully",
                this.userReportService.reportUser(username, request));
    }

    @Operation(summary = "Get my pending user reports", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/reports/users")
    public ResponseData<List<UserReportResponse>> getMyPendingUserReports() {
        return new ResponseData<>(HttpStatus.OK.value(), "success",
                this.userReportService.getMyPendingReports());
    }
}
