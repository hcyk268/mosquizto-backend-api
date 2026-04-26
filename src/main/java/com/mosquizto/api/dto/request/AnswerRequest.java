package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class AnswerRequest implements Serializable {

    @NotBlank
    private String term;

    @NotBlank
    private String definition;

    private Double responseTime;
}
