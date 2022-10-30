package it.winter2223.bachelor.ak.backend.authentication.dto.google;

public record GoogleSignUpResponse(
        String idToken,
        String email,
        String refreshToken,
        String expiresIn,
        String localId) {
}
