package com.mosquizto.api.service;

import com.mosquizto.api.model.User;

public interface CurrentUserProvider {

    String getCurrentUsername();

    User getCurrentUser();
}
