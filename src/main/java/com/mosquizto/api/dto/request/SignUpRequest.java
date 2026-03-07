package com.mosquizto.api.dto.request;

import com.mosquizto.api.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SignUpRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "email must be not blank")
    @Email
    private String email;

    @NotBlank(message = "password must be not blank")
    @StrongPassword
    private String password;

    @NotBlank(message = "confirmPassword must be not blank")
    @StrongPassword
    private String confirmPassword;
}
