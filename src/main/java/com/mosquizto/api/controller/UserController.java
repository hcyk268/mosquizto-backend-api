package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseData<Long> addUser(@Valid @RequestBody AddUserRequest request) {
        long userId = this.userService.addUser(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Add user success", userId);
    }

    @GetMapping("/confirm/{userId}")
    public ResponseData<?> confirmUser(@PathVariable long userId, @RequestParam String verifyCode) {
        this.userService.confirmUser(userId, verifyCode);
        return new ResponseData<>(HttpStatus.OK.value(), "User confirmed successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseData<PageResponse<UserResponse>> getListUser(
            @RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "Page must be greater than 0") int page,
            @RequestParam(defaultValue = "20", required = false) @Min(value = 10, message = "Size must be greater than 10") int size) {
        PageResponse<UserResponse> result = this.userService.getListUser(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user list success", result);
    }

    @PatchMapping("/change-password")
    public ResponseData<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        this.userService.changePassword(changePasswordRequest, request);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success");
    }

    @GetMapping("/profile")
    public ResponseData<UserResponse> getProfile(HttpServletRequest request) {
        UserResponse profile = this.userService.getProfile(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Get profile success", profile);
    }

    @PatchMapping("/update")
    public ResponseData<String> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest request) {
        this.userService.updateUser(updateUserRequest, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Update user success");
    }

}

