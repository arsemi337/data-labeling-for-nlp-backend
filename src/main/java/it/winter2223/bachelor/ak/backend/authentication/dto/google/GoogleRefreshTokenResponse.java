package it.winter2223.bachelor.ak.backend.authentication.dto.google;

public record GoogleRefreshTokenResponse(
        String expires_in,
        String token_type,
        String refresh_token,
        String id_token,
        String user_id,
        String project_id) {
}
