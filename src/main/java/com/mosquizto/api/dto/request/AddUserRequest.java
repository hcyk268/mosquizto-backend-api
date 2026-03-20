package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class AddUserRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    @Size(max = 150, message = "fullName must not exceed 150 characters")
    private String fullName;

    @NotBlank(message = "email must be not blank")
    @Email(message = "email is invalid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "username must be not blank")
    @Size(max = 100, message = "username must not exceed 100 characters")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotNull(message = "role must be not null")
    @Pattern(regexp = "USER|ADMIN")
    private String role;
}
