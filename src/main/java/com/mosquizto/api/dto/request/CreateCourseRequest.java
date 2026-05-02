package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
public class CreateCourseRequest implements Serializable {

    @NotBlank(message = "title must be not blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "description must be not blank")
    private String description;

    @NotNull(message = "visibility must be not null")
    private Boolean visibility;

    @Size(max = 255, message = "thumbnailUrl must not exceed 255 characters")
    private String thumbnailUrl;
}
