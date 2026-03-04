package com.mosquizto.api.service.impl;

import com.mosquizto.api.model.Token;
import com.mosquizto.api.repository.TokenRepository;
import com.mosquizto.api.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public void save(String username, String accessToken, String refreshToken) {
        Optional<Token> optionalToken = this.tokenRepository.findByUsername(username);

        Token token;
        if (optionalToken.isEmpty()) {
            token = Token.builder()
                    .username(username)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            token = optionalToken.get();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
        }
        this.tokenRepository.save(token);
    }
}
