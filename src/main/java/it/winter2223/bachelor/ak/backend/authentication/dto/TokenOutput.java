package it.winter2223.bachelor.ak.backend.authentication.dto;

import lombok.Builder;

@Builder
public record TokenOutput(
        String value,
        Long expiresIn
) {
}
