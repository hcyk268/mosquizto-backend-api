package com.mosquizto.api.exception;

import lombok.Getter;

@Getter
public class LimitExceedException extends RuntimeException {

    private final Long retryAfterSeconds;

    public LimitExceedException(String message) {
        this(message, null);
    }

    public LimitExceedException(String message, Long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
