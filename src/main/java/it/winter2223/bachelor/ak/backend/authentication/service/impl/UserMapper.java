package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserRoleOutput;
import it.winter2223.bachelor.ak.backend.authentication.model.User;

public class UserMapper {

    public UserOutput mapToUserOutput(User user, String jwtToken) {
        return UserOutput.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userRoleOutput(UserRoleOutput.valueOf(user.getUserRole().name()))
                .accessToken(jwtToken)
                .expiresIn("360000000000000")
                .refreshToken("")
                .build();
    }
}
