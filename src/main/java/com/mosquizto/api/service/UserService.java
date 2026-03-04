package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.model.User;

public interface UserService {

    User getByUsername(String username);

    long addUser(AddUserRequest request);
}
