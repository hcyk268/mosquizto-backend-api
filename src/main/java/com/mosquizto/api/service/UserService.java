package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.model.User;

public interface UserService {

    User getByUsername(String username);

    long addUser(AddUserRequest request);

    boolean checkEmailExists(String email);

    boolean checkUsernameExists(String username);

    void confirmUser(long userId, String verifyCode);

    void saveVerifyCode(long userId, String verifyCode);

    User getByEmail(String email);

    void save(User user);
}
