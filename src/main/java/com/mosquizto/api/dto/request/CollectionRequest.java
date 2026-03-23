package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CollectionRequest implements Serializable {
    @NotBlank(message = "title must be not blank")
    private String title;

    private String description;

    @NotNull(message = "visibility must be not null")
    private Boolean visibility;
}