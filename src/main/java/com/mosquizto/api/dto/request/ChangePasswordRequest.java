package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ChangePasswordRequest implements Serializable {

    @NotBlank(message = "Old password must be not blank")
    private String oldPassword;

    @NotBlank(message = "New password must be not blank")
    private String newPassword;
}
