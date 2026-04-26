package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class AnswerRequest implements Serializable {

    @NotNull
    private Boolean mode; // true: TERM -> DEFINITION, false: DEFINITION -> TERM

    @NotBlank
    private String term;

    @NotBlank
    private String definition;

    private Integer responseTime;
}
