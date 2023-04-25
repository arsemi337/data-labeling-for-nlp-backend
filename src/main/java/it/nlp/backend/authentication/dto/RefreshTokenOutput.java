package it.nlp.backend.authentication.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RefreshTokenOutput(
        UUID userId,
        TokenOutput accessTokenOutput,
        TokenOutput refreshTokenOutput
) {
}
