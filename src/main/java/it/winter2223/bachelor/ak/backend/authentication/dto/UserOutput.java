package it.winter2223.bachelor.ak.backend.authentication.dto;

import lombok.Builder;

@Builder
public record UserOutput(String email, String userId, String idToken, String refreshToken) {
}
