package com.mosquizto.api.exception;

public class InvalidDataException extends AppException {

    public InvalidDataException(String message) {
        super(ErrorCode.INVALID_PAYLOAD, message);
    }

    public InvalidDataException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
