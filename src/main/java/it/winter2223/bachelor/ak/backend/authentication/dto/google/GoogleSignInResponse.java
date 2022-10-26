package it.winter2223.bachelor.ak.backend.authentication.dto.google;

public record GoogleSignInResponse(
        String idToken,
        String email,
        String refreshToken,
        String expiresIn,
        String localId,
        boolean registered) {
}
