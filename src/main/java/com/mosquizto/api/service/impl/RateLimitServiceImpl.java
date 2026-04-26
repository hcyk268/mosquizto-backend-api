package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.LimitExceedException;
import com.mosquizto.api.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void rateLimit(String identify, String action, int maxReq, Duration windowDuration) {
        String key = "rateLimit:" + identify + ":" + action;

        long now = this.currentTimeMillis();
        long windowMillis = windowDuration.toMillis();
        long windowStart = now - windowMillis;
        ZSetOperations<String, Object> zSetOperations = this.redisTemplate.opsForZSet();

        zSetOperations.removeRangeByScore(key, 0, windowStart);
        Long count = zSetOperations.zCard(key);

        if (count != null && count >= maxReq) {
            long retryAfterMillis = this.calculateRetryAfterMillis(zSetOperations, key, now, windowMillis);
            long retryAfterSeconds = Math.max(1L, (retryAfterMillis + 999L) / 1000L);
            throw new LimitExceedException("Too Many Requests", retryAfterSeconds);
        }

        String member = identify + ":" + UUID.randomUUID();
        zSetOperations.add(key, member, now);
        this.redisTemplate.expire(key, windowDuration);
    }

    long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private long calculateRetryAfterMillis(
            ZSetOperations<String, Object> zSetOperations,
            String key,
            long now,
            long windowMillis) {
        Set<ZSetOperations.TypedTuple<Object>> oldestRequests = zSetOperations.rangeWithScores(key, 0, 0);
        if (oldestRequests == null || oldestRequests.isEmpty()) {
            return windowMillis;
        }

        ZSetOperations.TypedTuple<Object> oldestRequest = oldestRequests.iterator().next();
        Double oldestScore = oldestRequest.getScore();
        if (oldestScore == null) {
            return windowMillis;
        }

        return Math.max(0L, oldestScore.longValue() + windowMillis - now);
    }
}
