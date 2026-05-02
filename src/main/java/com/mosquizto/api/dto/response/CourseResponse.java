package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CourseRole;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class CourseResponse implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Boolean visibility;

    private String thumbnailUrl;

    private Integer collectionCount;

    private Integer memberCount;

    private CourseRole currentUserRole;

}
