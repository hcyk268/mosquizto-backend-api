package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.LimitExceedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Test
    void shouldAllowRequestWhenLimitIsNotExceeded() {
        when(this.redisTemplate.opsForZSet()).thenReturn(this.zSetOperations);
        when(this.zSetOperations.zCard("rateLimit:203.0.113.10:auth:login")).thenReturn(2L);

        RateLimitServiceImpl service = spy(new RateLimitServiceImpl(this.redisTemplate));
        doReturn(100_000L).when(service).currentTimeMillis();

        assertDoesNotThrow(() -> service.rateLimit("203.0.113.10", "auth:login", 3, Duration.ofSeconds(60)));

        verify(this.zSetOperations).removeRangeByScore("rateLimit:203.0.113.10:auth:login", 0, 40_000L);
        verify(this.zSetOperations).add(eq("rateLimit:203.0.113.10:auth:login"), any(), eq(100_000D));
        verify(this.redisTemplate).expire("rateLimit:203.0.113.10:auth:login", Duration.ofSeconds(60));
    }

    @Test
    void shouldThrowLimitExceededWithRetryAfterSeconds() {
        when(this.redisTemplate.opsForZSet()).thenReturn(this.zSetOperations);
        when(this.zSetOperations.zCard("rateLimit:203.0.113.10:auth:login")).thenReturn(3L);

        @SuppressWarnings("unchecked")
        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getScore()).thenReturn(97_500D);
        when(this.zSetOperations.rangeWithScores("rateLimit:203.0.113.10:auth:login", 0, 0))
                .thenReturn(Set.of(tuple));

        RateLimitServiceImpl service = spy(new RateLimitServiceImpl(this.redisTemplate));
        doReturn(100_000L).when(service).currentTimeMillis();

        LimitExceedException exception = assertThrows(
                LimitExceedException.class,
                () -> service.rateLimit("203.0.113.10", "auth:login", 3, Duration.ofSeconds(5)));

        assertEquals("Too Many Requests", exception.getMessage());
        assertEquals(3L, exception.getRetryAfterSeconds());
        verify(this.zSetOperations, never()).add(eq("rateLimit:203.0.113.10:auth:login"), any(), any(Double.class));
    }
}
