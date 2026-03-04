package com.mosquizto.api.service;

public interface TokenService {

    void save(String username, String accessToken, String refreshToken);

}
