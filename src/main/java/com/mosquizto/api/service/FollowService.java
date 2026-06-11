package com.mosquizto.api.service;

import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserSummaryResponse;

public interface FollowService {
    void follow(String username);

    void unfollow(String username);

    PageResponse<UserSummaryResponse> getFollowers(int page, int size);

    PageResponse<UserSummaryResponse> getFollowing(int page, int size);
}
