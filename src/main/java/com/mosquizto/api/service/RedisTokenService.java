package com.mosquizto.api.service;

import com.mosquizto.api.model.RedisToken;

public interface RedisTokenService {
    RedisToken save(RedisToken redisToken);

    RedisToken getById(String id);

    void deleteById(String id);
}
