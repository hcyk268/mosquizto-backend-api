package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.ChangePasswordRequest;
import com.mosquizto.api.dto.request.UpdateUserRequest;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserResponse;
import com.mosquizto.api.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    User getByUsername(String username);

    long addUser(AddUserRequest request);

    boolean checkEmailExists(String email);

    boolean checkUsernameExists(String username);

    void confirmUser(long userId, String verifyCode);

    void saveVerifyCode(long userId, String verifyCode);

    User getByEmail(String email);

    void save(User user);

    PageResponse<UserResponse> getListUser(int page, int size);

    void changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request);

    UserResponse getProfile(HttpServletRequest request);

    void updateUser(UpdateUserRequest updateUserRequest, HttpServletRequest request);
}
