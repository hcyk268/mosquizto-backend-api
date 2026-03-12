package com.mosquizto.api.dto.request;

import com.mosquizto.api.validator.StrongPassword;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ResetPasswordRequest implements Serializable {

    private String secretKey;

    @StrongPassword
    private String newPassword;

    @StrongPassword
    private String confirmPassword;
}
