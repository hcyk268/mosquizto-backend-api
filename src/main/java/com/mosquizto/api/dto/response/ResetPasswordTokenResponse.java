package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ResetPasswordTokenResponse implements Serializable {
    private String email;
    private String secretKey;
}
