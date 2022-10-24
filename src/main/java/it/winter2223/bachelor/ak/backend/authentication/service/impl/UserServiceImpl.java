package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        String body = String.format("""
                {
                    "email":"%s",
                    "password":"%s",
                    "returnSecureToken":true
                }
                """, userInput.email(), userInput.password());

        GoogleSignUpResponse response = client.post()
                .uri(String.format("""
                        https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=%s
                        """, firebaseApiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.value() != 200,
                        error -> Mono.error(new Exception(error.toString())))
                .bodyToMono(GoogleSignUpResponse.class)
                .block();

        return new UserOutput(response.email(), response.idToken(), response.refreshToken());
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        return null;
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        return null;
    }
}
