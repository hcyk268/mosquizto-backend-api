package com.mosquizto.api.service;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Supplier;

public interface IdempotencyService {
    <T extends Serializable> T execute(
            Long userId,
            String idempotencyKey,
            String operation,
            String fingerprint,
            Duration resultTtl,
            Supplier<T> action
    );
}
