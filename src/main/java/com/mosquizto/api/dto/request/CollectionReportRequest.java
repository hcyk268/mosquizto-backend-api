package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CollectionReportRequest implements Serializable {

    @NotBlank(message = "reason must be not blank")
    @Size(max = 100, message = "reason must not exceed 100 characters")
    private String reason;

    @Size(max = 2000, message = "description must not exceed 2000 characters")
    private String description;
}
