package it.winter2223.bachelor.ak.backend.authentication.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserOutput(
        UUID userId,
        String email,
        UserRoleOutput userRoleOutput,
        TokenOutput accessTokenOutput,
        TokenOutput refreshTokenOutput
) {
}
