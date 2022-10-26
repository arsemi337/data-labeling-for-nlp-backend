package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import it.winter2223.bachelor.ak.backend.authentication.Permission;
import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.SOMETHING_WENT_WRONG;

@Service
public class UserServiceImpl implements UserService {

    private final FirebaseAuth firebaseAuth;

    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    private final WebClient client = WebClient.create();

    public UserServiceImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public UserOutput singUp(UserInput userInput) {
        GoogleSignUpResponse response = client.post()
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

        List<Permission> requestedPermissions = new ArrayList<>();
        requestedPermissions.add(Permission.USER_READ_WRITE);

        List<String> permissions = requestedPermissions
                .stream()
                .map(Enum::toString)
                .toList();

        Map<String, Object> claims = Map.of("custom_claims", permissions);

        try {
            firebaseAuth.setCustomUserClaims(response.localId(), claims);
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthenticationException(e.getMessage());
        }

        GoogleRefreshTokenResponse refreshTokenResponse = requestRefreshToken(
                new RefreshTokenInput(response.refreshToken()));

        return new UserOutput(
                response.email(),
                refreshTokenResponse.id_token(),
                refreshTokenResponse.refresh_token());
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        GoogleSignInResponse response = client.post()
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

        return new UserOutput(response.email(), response.idToken(), response.refreshToken());
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        GoogleRefreshTokenResponse response = requestRefreshToken(refreshTokenInput);

        return new RefreshTokenOutput(response.id_token(), response.refresh_token());
    }

    private static String getUserInputBody(UserInput userInput) {
        return String.format("""
                {
                    "email":"%s",
                    "password":"%s",
                    "returnSecureToken":true
                }
                """, userInput.email(), userInput.password());
    }

    private GoogleRefreshTokenResponse requestRefreshToken(RefreshTokenInput refreshTokenInput) {
        String body = String.format("""
                {
                    "grant_type":"refresh_token",
                    "refresh_token":"%s",
                }
                """, refreshTokenInput.refreshToken());

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
}
