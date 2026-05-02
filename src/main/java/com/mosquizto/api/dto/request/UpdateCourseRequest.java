package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
public class UpdateCourseRequest implements Serializable {

    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    private String description;

    private Boolean visibility;

    @Size(max = 255, message = "thumbnailUrl must not exceed 255 characters")
    private String thumbnailUrl;
}
