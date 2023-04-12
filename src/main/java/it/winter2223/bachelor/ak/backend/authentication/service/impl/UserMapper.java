package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.TokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserRoleOutput;
import it.winter2223.bachelor.ak.backend.authentication.model.User;

import static it.winter2223.bachelor.ak.backend.config.security.JwtService.ACCESS_TOKEN_EXPIRY_TIME;
import static it.winter2223.bachelor.ak.backend.config.security.JwtService.REFRESH_TOKEN_EXPIRY_TIME;

public class UserMapper {

    public UserOutput mapToUserOutput(User user, String accessTokenValue, String refreshTokenValue) {
        return UserOutput.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userRoleOutput(UserRoleOutput.valueOf(user.getUserRole().name()))
                .accessTokenOutput(TokenOutput.builder()
                        .value(accessTokenValue)
                        .expiresIn(ACCESS_TOKEN_EXPIRY_TIME)
                        .build())
                .refreshTokenOutput(TokenOutput.builder()
                        .value(refreshTokenValue)
                        .expiresIn(REFRESH_TOKEN_EXPIRY_TIME)
                        .build())
                .build();
    }

    public RefreshTokenOutput mapToRefreshTokenOutput(String usedId, String accessTokenValue, String refreshTokenValue) {
        return RefreshTokenOutput.builder()
                .userId(usedId)
                .accessTokenOutput(TokenOutput.builder()
                        .value(accessTokenValue)
                        .expiresIn(ACCESS_TOKEN_EXPIRY_TIME)
                        .build())
                .refreshTokenOutput(TokenOutput.builder()
                        .value(refreshTokenValue)
                        .expiresIn(REFRESH_TOKEN_EXPIRY_TIME)
                        .build())
                .build();
    }
}
