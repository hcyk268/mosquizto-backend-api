package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserReportRequest implements Serializable {

    @NotBlank(message = "reason must not be blank")
    private String reason;

    private String description;
}
