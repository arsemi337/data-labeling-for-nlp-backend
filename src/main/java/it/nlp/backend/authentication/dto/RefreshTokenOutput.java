package it.nlp.backend.authentication.dto;

import lombok.Builder;

@Builder
public record RefreshTokenOutput(
        String userId,
        TokenOutput accessTokenOutput,
        TokenOutput refreshTokenOutput
) {
}
