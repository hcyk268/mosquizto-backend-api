package com.mosquizto.api.service;

import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserAchievementResponse;
import com.mosquizto.api.dto.response.UserActivityResponse;
import com.mosquizto.api.dto.response.UserStreakResponse;

import java.util.List;

public interface UserEngagementService {
    UserStreakResponse getStreak();

    List<UserAchievementResponse> getAchievements();

    PageResponse<UserActivityResponse> getActivity(int page, int size);
}
