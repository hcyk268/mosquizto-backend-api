package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserAchievementResponse;
import com.mosquizto.api.dto.response.UserActivityResponse;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.dto.response.UserStreakResponse;
import com.mosquizto.api.service.UserEngagementService;
import com.mosquizto.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User admin and profile APIs")
public class UserController {

    private final UserService userService;
    private final UserEngagementService userEngagementService;

    @Operation(summary = "Add user", description = "Admin creates a user account.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User created")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseData<Long> addUser(@Valid @RequestBody AddUserRequest request) {
        long userId = this.userService.addUser(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Add user success", userId);
    }

    @Operation(summary = "Confirm user", description = "Activate account using email verify code.", security = {})
    @ApiResponse(responseCode = "200", description = "User confirmed")
    @GetMapping("/confirm/{userId}")
    public ResponseData<?> confirmUser(
            @Parameter(description = "User ID", example = "42", required = true)
            @PathVariable long userId,
            @Parameter(description = "Email verify code", required = true)
            @RequestParam String verifyCode) {
        this.userService.confirmUser(userId, verifyCode);
        return new ResponseData<>(HttpStatus.OK.value(), "User confirmed successfully");
    }

    @Operation(summary = "List users", description = "Admin paginated user list.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Users returned")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseData<PageResponse<UserResponse>> getListUser(
            @Parameter(description = "Page number", example = "1")
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20", required = false) @Min(value = 10, message = "Size must be greater than 10") int size) {
        PageResponse<UserResponse> result = this.userService.getListUser(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user list success", result);
    }

    @Operation(summary = "Change password", description = "Change current user's password.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Password changed")
    @PatchMapping("/change-password")
    public ResponseData<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        this.userService.changePassword(changePasswordRequest);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    @Operation(summary = "Get profile", description = "Return current user profile.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile returned")
    @GetMapping("/profile")
    public ResponseData<UserResponse> getProfile() {
        UserResponse profile = this.userService.getProfile();
        return new ResponseData<>(HttpStatus.OK.value(), "Get profile success", profile);
    }

    @Operation(summary = "Get current user's study streak", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Study streak returned")
    @GetMapping("/streak")
    public ResponseData<UserStreakResponse> getStreak() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user streak success",
                this.userEngagementService.getStreak());
    }

    @Operation(summary = "Get current user's achievements", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Achievements returned")
    @GetMapping("/achievements")
    public ResponseData<List<UserAchievementResponse>> getAchievements() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user achievements success",
                this.userEngagementService.getAchievements());
    }

    @Operation(summary = "Get current user's activity timeline", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Activity timeline returned")
    @GetMapping("/activity")
    public ResponseData<PageResponse<UserActivityResponse>> getActivity(
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @RequestParam(defaultValue = "20", required = false) @Min(value = 1, message = "Size must be greater than 0") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user activity success",
                this.userEngagementService.getActivity(page, size));
    }

    @Operation(summary = "Update profile", description = "Update current user profile.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PatchMapping("/update")
    public ResponseData<String> updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        this.userService.updateUser(updateUserRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Update user success");
    }
}
