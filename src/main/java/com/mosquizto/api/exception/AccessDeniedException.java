package com.mosquizto.api.exception;

public class AccessDeniedException extends AppException {

    public AccessDeniedException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }

    public AccessDeniedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(ErrorCode.ACCESS_DENIED, message, cause);
    }
}
