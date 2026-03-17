package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "APIs for user management: add, confirm, list, profile, update, and change password")
public class UserController {

    private final UserService userService;

    // ────────────────────────────────────────────────────────────── ADD USER ──

    @Operation(
            summary = "Add a new user (Admin)",
            description = """
                    Creates a new user account directly (admin-side operation).
                    Unlike `/auth/register`, this endpoint does **not** send a confirmation email.
                    
                    **role** must be either `USER` or `ADMIN`.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully, returns the new user ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Add user success",
                                      "data": 7
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payload (missing field, invalid email, invalid role...)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Missing field", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/user/add",
                                              "error": "Invalid Payload",
                                              "message": "fullName must be not blank"
                                            }
                                            """),
                                    @ExampleObject(name = "Invalid role", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/user/add",
                                              "error": "Invalid Payload",
                                              "message": "role: must match \\"USER|ADMIN\\""
                                            }
                                            """)
                            })
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing or invalid access token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/user/add",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            )
    })
    @PostMapping("/add")
    public ResponseData<Long> addUser(@Valid @RequestBody AddUserRequest request) {
        long userId = this.userService.addUser(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Add user success", userId);
    }

    // ──────────────────────────────────────────────────────── CONFIRM USER ──

    @Operation(
            summary = "Confirm email address",
            description = """
                    Activates a user account by verifying the email confirmation link.
                    This endpoint is called automatically when the user clicks the link
                    sent to their email after registration.
                    
                    The `userId` and `verifyCode` are embedded in the confirmation link.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account confirmed and activated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "User confirmed successfully"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or already-used verify code",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 400,
                                      "path": "/user/confirm/42",
                                      "error": "Invalid Payload",
                                      "message": "Verify code is invalid"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 404,
                                      "path": "/user/confirm/42",
                                      "error": "Not Found",
                                      "message": "User not found with id: 42"
                                    }
                                    """))
            )
    })
    @GetMapping("/confirm/{userId}")
    public ResponseData<?> confirmUser(
            @Parameter(description = "ID of the user to confirm", example = "42", required = true)
            @PathVariable long userId,
            @Parameter(description = "Verification code from the confirmation email", required = true)
            @RequestParam String verifyCode) {
        this.userService.confirmUser(userId, verifyCode);
        return new ResponseData<>(HttpStatus.OK.value(), "User confirmed successfully");
    }

    // ──────────────────────────────────────────────────────────── LIST USERS ──

    @Operation(
            summary = "Get paginated user list (Admin only)",
            description = """
                    Returns a paginated list of all users. **Requires ADMIN role.**
                    
                    | Param | Default | Min |
                    |-------|---------|-----|
                    | `page` | 1 | 1 |
                    | `size` | 20 | 10 |
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User list retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Get user list success",
                                      "data": {
                                        "page": 1,
                                        "size": 20,
                                        "totalElements": 2,
                                        "totalPages": 1,
                                        "items": [
                                          {
                                            "id": 1,
                                            "fullName": "John Doe",
                                            "email": "johndoe@example.com",
                                            "username": "johndoe",
                                            "status": "ACTIVE",
                                            "role": "USER",
                                            "createdAt": "2026-03-17T10:00:00.000+00:00",
                                            "updatedAt": "2026-03-17T10:00:00.000+00:00"
                                          },
                                          {
                                            "id": 2,
                                            "fullName": "Admin User",
                                            "email": "admin@example.com",
                                            "username": "admin",
                                            "status": "ACTIVE",
                                            "role": "ADMIN",
                                            "createdAt": "2026-03-17T08:00:00.000+00:00",
                                            "updatedAt": "2026-03-17T08:00:00.000+00:00"
                                          }
                                        ]
                                      }
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 400,
                                      "path": "/user/list",
                                      "error": "Invalid Payload",
                                      "message": "getListUser.size: Size must be greater than 10"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing or invalid access token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/user/list",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — caller does not have ADMIN role",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 403,
                                      "path": "/user/list",
                                      "error": "Forbidden",
                                      "message": "Access Denied"
                                    }
                                    """))
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseData<PageResponse<UserResponse>> getListUser(
            @Parameter(description = "Page number (1-indexed)", example = "1")
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(description = "Number of items per page (minimum 10)", example = "20")
            @RequestParam(defaultValue = "20", required = false) @Min(value = 10, message = "Size must be greater than 10") int size) {
        PageResponse<UserResponse> result = this.userService.getListUser(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user list success", result);
    }

    // ─────────────────────────────────────────────────────── CHANGE PASSWORD ──

    @Operation(
            summary = "Change password",
            description = """
                    Changes the current user's password. Requires the correct **old password**.
                    The user is identified from the `Authorization` header (access token).
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 204,
                                      "message": "Success"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Old password is incorrect or missing fields",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Wrong old password", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/user/change-password",
                                              "error": "Invalid Payload",
                                              "message": "Old password is incorrect"
                                            }
                                            """),
                                    @ExampleObject(name = "Missing field", value = """
                                            {
                                              "timestamp": "2026-03-17T10:00:00.000+00:00",
                                              "status": 400,
                                              "path": "/user/change-password",
                                              "error": "Invalid Payload",
                                              "message": "Old password must be not blank"
                                            }
                                            """)
                            })
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing or invalid access token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/user/change-password",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            )
    })
    @PatchMapping("/change-password")
    public ResponseData<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            HttpServletRequest request) {
        this.userService.changePassword(changePasswordRequest, request);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    // ────────────────────────────────────────────────────────────── PROFILE ──

    @Operation(
            summary = "Get current user profile",
            description = """
                    Returns the profile of the currently authenticated user.
                    The user is identified from the `Authorization` header (access token).
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Get profile success",
                                      "data": {
                                        "id": 42,
                                        "fullName": "John Doe",
                                        "email": "johndoe@example.com",
                                        "username": "johndoe",
                                        "status": "ACTIVE",
                                        "role": "USER",
                                        "createdAt": "2026-03-17T10:00:00.000+00:00",
                                        "updatedAt": "2026-03-17T10:00:00.000+00:00"
                                      }
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing or invalid access token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/user/profile",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            )
    })
    @GetMapping("/profile")
    public ResponseData<UserResponse> getProfile(HttpServletRequest request) {
        UserResponse profile = this.userService.getProfile(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Get profile success", profile);
    }

    // ─────────────────────────────────────────────────────────── UPDATE USER ──

    @Operation(
            summary = "Update user profile",
            description = """
                    Updates the profile information of the currently authenticated user.
                    The user is identified from the `Authorization` header (access token).
                    
                    Currently supports updating: `fullName`.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Update user success"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payload — fullName is blank",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 400,
                                      "path": "/user/update",
                                      "error": "Invalid Payload",
                                      "message": "fullName must be not blank"
                                    }
                                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing or invalid access token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-03-17T10:00:00.000+00:00",
                                      "status": 401,
                                      "path": "/user/update",
                                      "error": "Unauthorized",
                                      "message": "Token is required"
                                    }
                                    """))
            )
    })
    @PatchMapping("/update")
    public ResponseData<String> updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            HttpServletRequest request) {
        this.userService.updateUser(updateUserRequest, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Update user success");
    }
}
