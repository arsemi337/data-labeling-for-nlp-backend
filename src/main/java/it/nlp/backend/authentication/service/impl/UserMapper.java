package it.nlp.backend.authentication.service.impl;

import it.nlp.backend.authentication.dto.RefreshTokenOutput;
import it.nlp.backend.authentication.dto.TokenOutput;
import it.nlp.backend.authentication.dto.UserOutput;
import it.nlp.backend.authentication.model.User;
import it.nlp.backend.authentication.dto.UserRoleOutput;

import java.util.UUID;

public class UserMapper {

    public UserOutput mapToUserOutput(User user, String accessTokenValue, String refreshTokenValue) {
        return UserOutput.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userRoleOutput(UserRoleOutput.valueOf(user.getUserRole().name()))
                .accessTokenOutput(TokenOutput.builder()
                        .value(accessTokenValue)
                        .expiresIn(JwtService.ACCESS_TOKEN_EXPIRY_TIME)
                        .build())
                .refreshTokenOutput(TokenOutput.builder()
                        .value(refreshTokenValue)
                        .expiresIn(JwtService.REFRESH_TOKEN_EXPIRY_TIME)
                        .build())
                .build();
    }

    public RefreshTokenOutput mapToRefreshTokenOutput(UUID userId, String accessTokenValue, String refreshTokenValue) {
        return RefreshTokenOutput.builder()
                .userId(userId)
                .accessTokenOutput(TokenOutput.builder()
                        .value(accessTokenValue)
                        .expiresIn(JwtService.ACCESS_TOKEN_EXPIRY_TIME)
                        .build())
                .refreshTokenOutput(TokenOutput.builder()
                        .value(refreshTokenValue)
                        .expiresIn(JwtService.REFRESH_TOKEN_EXPIRY_TIME)
                        .build())
                .build();
    }
}
