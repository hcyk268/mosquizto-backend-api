package com.mosquizto.api.exception;

public class InvalidTokenException extends AppException {

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }

    public InvalidTokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(ErrorCode.INVALID_TOKEN, message, cause);
    }
}
