package com.mosquizto.api.service;

import com.mosquizto.api.dto.response.MediaSignResponse;

public interface MediaSignService {

    MediaSignResponse signForUser(Long userId, String folder);

    void validateAvatarUrl(Long userId, String avatarUrl);
}
