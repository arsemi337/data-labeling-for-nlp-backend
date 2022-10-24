package it.winter2223.bachelor.ak.backend.authentication.dto;

public record GoogleSignUpResponse(String kind, String idToken, String email, String refreshToken, String expiresIn, String localId) {
}
