package it.nlp.backend.authentication.dto;

import lombok.Builder;

@Builder
public record RefreshTokenInput(
        String refreshTokenValue
) {
}
