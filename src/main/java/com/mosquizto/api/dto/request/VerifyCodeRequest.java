package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VerifyCodeRequest implements Serializable {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}
