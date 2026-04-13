package com.mosquizto.api.service.impl;

import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.RedisToken;
import com.mosquizto.api.repository.RedisTokenRepository;
import com.mosquizto.api.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    @Override
    public RedisToken save(RedisToken redisToken) {
        return this.redisTokenRepository.save(redisToken);
    }

    @Override
    public RedisToken getById(String id) {
        return this.redisTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token Not Found"));
    }

    @Override
    public void deleteById(String id) {
        this.redisTokenRepository.deleteById(id);
    }
}
