package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class IdempotencyServiceImpl implements IdempotencyService {

    private static final String FINGERPRINT_FIELD = "fingerprint";
    private static final String RESULT_FIELD = "result";
    private static final Duration LOCK_TTL = Duration.ofSeconds(30);
    private static final int WAIT_ATTEMPTS = 10;
    private static final long WAIT_MILLIS = 100L;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T extends Serializable> T execute(Long userId, String idempotencyKey, String operation, String fingerprint, Duration resultTtl, Supplier<T> action) {
        this.validateInputs(userId, idempotencyKey, operation, resultTtl, action);

        String normalizedFingerprint = fingerprint == null ? "" : fingerprint;
        String normalizedKey = idempotencyKey.trim();
        String redisKey = this.buildIdempotencyKey(userId, normalizedKey, operation);

        T cachedResult = this.getCachedResult(redisKey, normalizedFingerprint, operation);
        if (cachedResult != null) {
            return cachedResult;
        }

        String lockKey = this.buildIdempotencyLockKey(redisKey);
        Boolean lockAcquired = this.redisTemplate.opsForValue().setIfAbsent(lockKey, normalizedFingerprint, LOCK_TTL);

        if (!Boolean.TRUE.equals(lockAcquired)) {
            this.ensureInFlightFingerprintMatches(lockKey, normalizedFingerprint, operation);

            T awaitedResult = this.waitForCachedResult(redisKey, normalizedFingerprint, operation);
            if (awaitedResult != null) {
                return awaitedResult;
            }

            throw new ConflictException(operation + " request is already being processed");
        }

        try {
            T cachedAfterLock = this.getCachedResult(redisKey, normalizedFingerprint, operation);
            if (cachedAfterLock != null) {
                return cachedAfterLock;
            }

            T result = action.get();
            this.cacheResult(redisKey, normalizedFingerprint, result, resultTtl);
            return result;
        } finally {
            this.redisTemplate.delete(lockKey);
        }
    }

    private void validateInputs(Long userId,
                                String idempotencyKey,
                                String operation,
                                Duration resultTtl,
                                Supplier<?> action) {
        if (userId == null) {
            throw new InvalidDataException("User id is required for idempotency");
        }

        if (!StringUtils.hasText(idempotencyKey)) {
            throw new InvalidDataException("Idempotency-Key header is required");
        }

        if (!StringUtils.hasText(operation)) {
            throw new InvalidDataException("Idempotency operation is required");
        }

        if (resultTtl == null || resultTtl.isZero() || resultTtl.isNegative()) {
            throw new InvalidDataException("Idempotency result TTL must be greater than 0");
        }

        Objects.requireNonNull(action, "Idempotency action is required");
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> T getCachedResult(String redisKey, String fingerprint, String operation) {
        Map<Object, Object> cached = this.redisTemplate.opsForHash().entries(redisKey);
        if (cached.isEmpty()) {
            return null;
        }

        Object cachedFingerprint = cached.get(FINGERPRINT_FIELD);
        if (cachedFingerprint == null) {
            return null;
        }

        this.ensureFingerprintMatches(cachedFingerprint.toString(), fingerprint, operation);

        Object cachedResult = cached.get(RESULT_FIELD);
        if (cachedResult == null) {
            return null;
        }

        return (T) cachedResult;
    }

    private <T extends Serializable> T waitForCachedResult(String redisKey, String fingerprint, String operation) {
        for (int attempt = 0; attempt < WAIT_ATTEMPTS; attempt++) {
            T cachedResult = this.getCachedResult(redisKey, fingerprint, operation);
            if (cachedResult != null) {
                return cachedResult;
            }

            this.sleepQuietly(operation);
        }

        return this.getCachedResult(redisKey, fingerprint, operation);
    }

    private void cacheResult(String redisKey, String fingerprint, Serializable result, Duration resultTtl) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(FINGERPRINT_FIELD, fingerprint);
        payload.put(RESULT_FIELD, result);

        this.redisTemplate.opsForHash().putAll(redisKey, payload);
        this.redisTemplate.expire(redisKey, resultTtl);
    }

    private void ensureInFlightFingerprintMatches(String lockKey, String fingerprint, String operation) {
        Object inFlightFingerprint = this.redisTemplate.opsForValue().get(lockKey);
        if (inFlightFingerprint != null) {
            this.ensureFingerprintMatches(inFlightFingerprint.toString(), fingerprint, operation);
        }
    }

    private void ensureFingerprintMatches(String actualFingerprint, String expectedFingerprint, String operation) {
        if (!actualFingerprint.equals(expectedFingerprint)) {
            throw new ConflictException("Idempotency-Key cannot be reused with a different " + operation + " payload");
        }
    }

    private String buildIdempotencyKey(Long userId, String idempotencyKey, String operation) {
        return "idempotency:" + operation + ":" + userId + ":" + idempotencyKey;
    }

    private String buildIdempotencyLockKey(String redisKey) {
        return redisKey + ":lock";
    }

    private void sleepQuietly(String operation) {
        try {
            Thread.sleep(WAIT_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InvalidDataException(operation + " request was interrupted");
        }
    }
}
