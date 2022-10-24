package it.winter2223.bachelor.ak.backend.authentication.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserOutput(String email, String idToken, String refreshToken) {
}
