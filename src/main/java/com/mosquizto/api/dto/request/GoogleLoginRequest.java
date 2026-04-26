package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GoogleLoginRequest implements Serializable {
    @NotBlank(message = "idToken must be not blank")
    private String idToken;
}
