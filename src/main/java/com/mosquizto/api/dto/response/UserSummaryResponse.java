package com.mosquizto.api.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class UserSummaryResponse implements Serializable {
    private String fullName;
    private String username;
    private String imgUri;
    private boolean followed;
    private long followersCount;
    private long followingCount;
}
