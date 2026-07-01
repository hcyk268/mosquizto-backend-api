package com.mosquizto.api.controller.users;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Admin Users", description = "Admin-only user management APIs")
public class AdminUsersController {

    private final UserService userService;

    @Operation(summary = "Add user", description = "Admin creates a user account.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User created")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseData<Long> addUser(@Valid @RequestBody AddUserRequest request) {
        long userId = this.userService.addUser(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Add user success", userId);
    }

    @Operation(summary = "List users", description = "Admin paginated user list.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Users returned")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(params = "!keyword")
    public ResponseData<PageResponse<UserResponse>> getListUser(
            @Parameter(description = "Page number", example = "1")
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20", required = false) @Min(value = 10, message = "Size must be greater than 10") int size) {
        PageResponse<UserResponse> result = this.userService.getListUser(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user list success", result);
    }

    @Operation(summary = "Delete user", description = "Delete current user or admin delete", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Delete user")
    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@Valid @Positive @PathVariable Long userId) {
        this.userService.deleteUser(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Delete user successfully");
    }
}
