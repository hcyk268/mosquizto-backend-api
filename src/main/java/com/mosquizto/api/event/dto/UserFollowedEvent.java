package com.mosquizto.api.event.dto;

public record UserFollowedEvent(
        Long followId,
        String targetUsername,
        String followerDisplayName
) {}
