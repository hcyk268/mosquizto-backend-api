package com.mosquizto.api.service;

import java.time.Duration;

public interface RateLimitService {
    void rateLimit(String identify, String action, int maxReq, Duration windowDuration);
}
