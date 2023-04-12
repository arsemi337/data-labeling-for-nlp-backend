package it.nlp.backend.authentication.dto;

import lombok.Builder;

@Builder
public record TokenOutput(
        String value,
        Long expiresIn
) {
}
