package com.mosquizto.api.exception;

import lombok.Getter;

@Getter
public class LimitExceedException extends AppException {

    private final Long retryAfterSeconds;

    public LimitExceedException(String message) {
        this(message, null);
    }

    public LimitExceedException(String message, Long retryAfterSeconds) {
        super(ErrorCode.RATE_LIMIT_EXCEEDED, message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
