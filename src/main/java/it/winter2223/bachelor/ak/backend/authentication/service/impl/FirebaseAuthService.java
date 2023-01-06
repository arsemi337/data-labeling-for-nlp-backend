package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import it.winter2223.bachelor.ak.backend.authentication.persistence.Permission;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.*;

@Service
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;
    private final WebClient client;

    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    public FirebaseAuthService(FirebaseAuth firebaseAuth, WebClient client) {
        this.firebaseAuth = firebaseAuth;
        this.client = client;
    }

    protected GoogleSignUpResponse signUpUser(UserInput userInput) {
        return client.post()
                .uri(String.format("""
                        https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=%s
                        """, firebaseApiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getUserInputBody(userInput)))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.value() != 200,
                        error -> Mono.error(new FirebaseAuthenticationException(SIGNING_UP_FAILED.getMessage())))
                .bodyToMono(GoogleSignUpResponse.class)
                .block();
    }

    protected GoogleSignInResponse signInUser(UserInput userInput) {
        return client.post()
                .uri(String.format("""
                        https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=%s
                        """, firebaseApiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getUserInputBody(userInput)))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.value() != 200,
                        error -> Mono.error(new FirebaseAuthenticationException(SIGNING_IN_FAILED.getMessage())))
                .bodyToMono(GoogleSignInResponse.class)
                .block();
    }

    protected GoogleRefreshTokenResponse requestRefreshToken(RefreshTokenInput refreshTokenInput) {
        String body = getRefreshTokenBody(refreshTokenInput.refreshToken());

        return client.post()
                .uri(String.format("""
                        https://securetoken.googleapis.com/v1/token?key=%s
                        """, firebaseApiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.value() != 200,
                        error -> Mono.error(new FirebaseAuthenticationException(TOKEN_REFRESHING_FAILED.getMessage())))
                .bodyToMono(GoogleRefreshTokenResponse.class)
                .block();
    }

    private String getUserInputBody(UserInput userInput) {
        return String.format("""
                {
                    "email":"%s",
                    "password":"%s",
                    "returnSecureToken":true
                }
                """, userInput.email(), userInput.password());
    }

    private String getRefreshTokenBody(String refreshToken) {
        return String.format("""
                {
                    "grant_type":"refresh_token",
                    "refresh_token":"%s"
                }
                """, refreshToken);
    }

    public void setCustomUserClaims(String localId) {
        Map<String, Object> claims = Map.of("custom_claims", List.of(Permission.USER_READ_WRITE.toString()));

        try {
            firebaseAuth.setCustomUserClaims(localId, claims);
        } catch (FirebaseAuthException exception) {
            throw new FirebaseAuthenticationException(SETTING_USER_CLAIMS_FAILED.getMessage(), exception);
        }
    }
}
