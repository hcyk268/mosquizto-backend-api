package com.mosquizto.api.dto.request;

import com.mosquizto.api.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SignUpRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    @Size(max = 150, message = "fullName must not exceed 150 characters")
    private String fullName;

    @NotBlank(message = "username must be not blank")
    @Size(max = 100, message = "username must not exceed 100 characters")
    private String username;

    @NotBlank(message = "email must be not blank")
    @Email(message = "Email form invalid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "password must be not blank")
    @StrongPassword
    private String password;

    @NotBlank(message = "confirmPassword must be not blank")
    @StrongPassword
    private String confirmPassword;
}
