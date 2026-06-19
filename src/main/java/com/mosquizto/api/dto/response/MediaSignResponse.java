package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class MediaSignResponse implements Serializable {

    private String cloudName;
    private String apiKey;
    private long timestamp;
    private String signature;
    private String folder;
    private String publicId;
}
