package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import it.winter2223.bachelor.ak.backend.authentication.Permission;
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

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.SOMETHING_WENT_WRONG;

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
                        error -> Mono.error(new FirebaseAuthenticationException(SOMETHING_WENT_WRONG.getMessage())))
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
                        error -> Mono.error(new FirebaseAuthenticationException(SOMETHING_WENT_WRONG.getMessage())))
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
                        error -> Mono.error(new FirebaseAuthenticationException(SOMETHING_WENT_WRONG.getMessage())))
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
                    "refresh_token":"%s",
                }
                """, refreshToken);
    }

    public void setCustomUserClaims(GoogleSignUpResponse response) {
        Map<String, Object> claims = Map.of("custom_claims", List.of(Permission.USER_READ_WRITE.toString()));

        try {
            firebaseAuth.setCustomUserClaims(response.localId(), claims);
        } catch (FirebaseAuthException exception) {
            throw new FirebaseAuthenticationException(SOMETHING_WENT_WRONG.getMessage(), exception);
        }
    }
}
