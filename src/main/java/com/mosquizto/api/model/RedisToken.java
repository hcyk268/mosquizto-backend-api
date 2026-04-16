package com.mosquizto.api.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("RedisToken")
public class RedisToken implements Serializable {
    private String id; //username of User
    private String accessToken;
    private String refreshToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long timeToLive;

    public static RedisToken initiate(String id, String accessToken, String refreshToken, long ttlSeconds) {
        return RedisToken.builder()
                .id(id)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .timeToLive(ttlSeconds)
                .build();
    }
}
