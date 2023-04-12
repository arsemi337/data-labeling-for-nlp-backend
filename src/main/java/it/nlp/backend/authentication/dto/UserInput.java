package it.nlp.backend.authentication.dto;

import lombok.Builder;

@Builder
public record UserInput(
        String email,
        String password
) {
}
