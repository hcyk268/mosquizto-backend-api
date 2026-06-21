package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.FollowNotificationResponse;
import com.mosquizto.api.dto.response.UserReportResponse;
import com.mosquizto.api.model.Follow;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserReport;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;

@Component
public class UserReportMapper {

    public UserReportResponse toResponse(UserReport report) {
        return UserReportResponse.builder()
                .id(report.getId())
                .reportedUserId(report.getReportedUser() != null ? report.getReportedUser().getId() : null)
                .reporterId(report.getReporter() != null ? report.getReporter().getId() : null)
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createAt(toIso(report.getCreatedAt()))
                .updateAt(toIso(report.getUpdatedAt()))
                .build();
    }

    public FollowNotificationResponse toFollowNotificationResponse(Follow follow) {
        User follower = follow.getFollower();
        return FollowNotificationResponse.builder()
                .id(follow.getId())
                .followerId(follower != null ? follower.getId() : null)
                .followerUsername(follower != null ? follower.getUsername() : null)
                .followerFullName(follower != null ? follower.getFullName() : null)
                .followerImgUri(follower != null ? follower.getAvatarUrl() : null)
                .followedAt(toIso(follow.getCreatedAt()))
                .build();
    }

    private static String toIso(Date date) {
        if (date == null) {
            return null;
        }

        return Instant.ofEpochMilli(date.getTime()).toString();
    }

    public static String displayName(User user) {
        if (user == null) {
            return null;
        }

        if (StringUtils.hasText(user.getFullName())) {
            return user.getFullName();
        }

        return user.getUsername();
    }
}
