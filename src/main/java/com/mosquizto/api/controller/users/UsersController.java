package com.mosquizto.api.controller.users;

import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateAvatarRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.service.FollowService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User admin and profile APIs")
public class UsersController {

    private final UserService userService;
    private final UserEngagementService userEngagementService;
    private final FollowService followService;

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

    @Operation(summary = "Change password", description = "Change current user's password.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Password changed")
    @PatchMapping("/me/password")
    public ResponseData<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        this.userService.changePassword(changePasswordRequest);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    @Operation(summary = "Get profile", description = "Return current user profile.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile returned")
    @GetMapping("/me")
    public ResponseData<UserResponse> getProfile() {
        UserResponse profile = this.userService.getProfile();
        return new ResponseData<>(HttpStatus.OK.value(), "Get profile success", profile);
    }

    @Operation(summary = "Get avatar", description = "Return current user's avatar URL.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Avatar returned")
    @GetMapping("/me/avatar")
    public ResponseData<AvatarResponse> getAvatar() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get avatar success", this.userService.getAvatar());
    }

    @Operation(summary = "Update avatar", description = "Save Cloudinary avatar URL after signed upload.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Avatar updated")
    @PatchMapping("/me/avatar")
    public ResponseData<String> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        this.userService.updateAvatarUrl(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Update avatar success");
    }

    @Operation(summary = "Get current user's study streak", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Study streak returned")
    @GetMapping("/me/streak")
    public ResponseData<UserStreakResponse> getStreak() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user streak success",
                this.userEngagementService.getStreak());
    }

    @Operation(summary = "Get current user's achievements", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Achievements returned")
    @GetMapping("/me/achievements")
    public ResponseData<List<UserAchievementResponse>> getAchievements() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user achievements success",
                this.userEngagementService.getAchievements());
    }

    @Operation(summary = "Get current user's activity timeline", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Activity timeline returned")
    @GetMapping("/me/activity")
    public ResponseData<PageResponse<UserActivityResponse>> getActivity(
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @RequestParam(defaultValue = "20", required = false) @Min(value = 1, message = "Size must be greater than 0") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get user activity success",
                this.userEngagementService.getActivity(page, size));
    }

    @Operation(summary = "Update profile", description = "Update current user profile.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PatchMapping("/me")
    public ResponseData<String> updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        this.userService.updateUser(updateUserRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Update user success");
    }

    @Operation(summary = "Search user", description = "search similarity username", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Search user")
    @GetMapping(params = "keyword")
    public ResponseData<PageResponse<
            UserSummaryResponse>> searchUser(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        page = page > 0 ? page - 1 : 0;
        return new ResponseData<>(HttpStatus.OK.value(), "Success",userService.searchUsers(keyword,page,size));
    }

    @Operation(summary = "Get user profile by username",
            description = "Return public profile summary and whether the current user follows this profile.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User profile returned")
    @GetMapping("/{username}")
    public ResponseData<UserSummaryResponse> getUser(
            @Parameter(description = "Username", example = "teacher_lan_ielts", required = true)
            @PathVariable String username) {
        return new ResponseData<>(HttpStatus.OK.value(), "Successfully", this.userService.getUser(username));
    }

    @Operation(summary = "Get current user's followers",
            description = "Return a paginated list of users who follow the current user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Followers returned")
    @GetMapping("/me/followers")
    public ResponseData<PageResponse<UserSummaryResponse>> getFollowers(
            @RequestParam(defaultValue = "1", required = false)
            @Min(value = 1, message = "Page must be greater than 0") int page,
            @RequestParam(defaultValue = "20", required = false)
            @Min(value = 1, message = "Size must be greater than 0") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get followers success",
                this.followService.getFollowers(page, size));
    }

    @Operation(summary = "Get users followed by current user",
            description = "Return a paginated list of users followed by the current user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Following users returned")
    @GetMapping("/me/following")
    public ResponseData<PageResponse<UserSummaryResponse>> getFollowing(
            @RequestParam(defaultValue = "1", required = false)
            @Min(value = 1, message = "Page must be greater than 0") int page,
            @RequestParam(defaultValue = "20", required = false)
            @Min(value = 1, message = "Size must be greater than 0") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get following success",
                this.followService.getFollowing(page, size));
    }

    @Operation(summary = "Follow user",
            description = "Follow the user identified by username.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User followed")
    @PostMapping("/{username}/follow")
    public ResponseData<Void> follow(
            @Parameter(description = "Username to follow", example = "teacher_lan_ielts", required = true)
            @PathVariable String username) {
        this.followService.follow(username);
        return new ResponseData<>(HttpStatus.OK.value(), "Successfully");
    }

    @Operation(summary = "Get follow notifications",
            description = "Return users who recently followed the current user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Follow notifications returned")
    @GetMapping("/me/follow-notifications")
    public ResponseData<List<FollowNotificationResponse>> getFollowNotifications() {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                this.followService.getFollowNotifications());
    }

    @Operation(summary = "Unfollow user",
            description = "Unfollow the user identified by username.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User unfollowed")
    @DeleteMapping("/{username}/follow")
    public ResponseData<Void> unfollow(
            @Parameter(description = "Username to unfollow", example = "teacher_lan_ielts", required = true)
            @PathVariable String username) {
        this.followService.unfollow(username);
        return new ResponseData<>(HttpStatus.OK.value(), "Successfully");
    }
}
