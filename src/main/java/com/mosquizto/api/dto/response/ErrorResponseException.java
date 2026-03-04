package com.mosquizto.api.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ErrorResponseException implements Serializable {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private String message;
}
