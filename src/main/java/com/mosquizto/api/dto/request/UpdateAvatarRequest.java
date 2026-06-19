package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UpdateAvatarRequest implements Serializable {

    @NotBlank(message = "avatarUrl must not be blank")
    private String avatarUrl;
}
