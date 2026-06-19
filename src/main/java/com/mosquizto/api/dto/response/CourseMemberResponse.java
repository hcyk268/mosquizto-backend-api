package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class CourseMemberResponse implements Serializable {

    private Long userId;

    private String username;

    private String fullName;

    private String imgUri;

    private CourseRole role;

    private AccessStatus status;

    private Date joinedAt;
}
