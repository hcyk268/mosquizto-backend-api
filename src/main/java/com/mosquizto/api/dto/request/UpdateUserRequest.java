package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UpdateUserRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    private String fullName;
}
