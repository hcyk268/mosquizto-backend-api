package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.request.AddUserRequest;
import com.mosquizto.api.dto.request.SignUpRequest;
import com.mosquizto.api.dto.response.ResetPasswordTokenResponse;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper {

    public TokenResponse toTokenResponse(User user, String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public ResetPasswordTokenResponse toResetPasswordTokenResponse(String email, String secretKey) {
        return ResetPasswordTokenResponse.builder()
                .email(email)
                .secretKey(secretKey)
                .build();
    }

    public AddUserRequest toAddUserRequest(SignUpRequest signUpRequest) {
        return AddUserRequest.builder()
                .fullName(signUpRequest.getFullName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .role("USER")
                .build();
    }
}
