package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class JoinResponse implements Serializable {

    private Long id;

    private Long userId;

    private Long courseId;

    private String titleCourse;

    private AccessStatus status;

    private CourseRole role;

}
