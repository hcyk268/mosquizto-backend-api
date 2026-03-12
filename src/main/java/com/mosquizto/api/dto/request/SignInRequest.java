package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;
}
