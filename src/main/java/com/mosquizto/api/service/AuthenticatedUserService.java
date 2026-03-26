package com.mosquizto.api.service;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticatedUserService {
    User getAuthenticatedUser(HttpServletRequest httpServletRequest);
    Boolean isAuthorOfCollection(HttpServletRequest httpServletRequest, Collection collection) ;
}
