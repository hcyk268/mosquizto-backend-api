package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class AvatarResponse implements Serializable {

    private String imgUri;
}
