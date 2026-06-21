package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class FollowNotificationResponse implements Serializable {

    private Long id;
    private Long followerId;
    private String followerUsername;
    private String followerFullName;
    private String followerImgUri;
    private String followedAt;
}
