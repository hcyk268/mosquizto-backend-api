package com.mosquizto.api.service;

import com.mosquizto.api.model.Token;

public interface TokenService {

    void save(String username, String accessToken, String refreshToken);

    void deleteById(long id);

    void deleteByUsername(String username);

    Token getByUsername(String username);
}
