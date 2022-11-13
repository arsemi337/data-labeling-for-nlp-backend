package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_EMAIL_ADDRESS;
import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_PASSWORD;

@Service
public class UserServiceImpl implements UserService {

    private final FirebaseAuthService firebaseAuthService;


    public UserServiceImpl(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @Override
    public UserOutput singUp(UserInput userInput) {
        validateEmail(userInput.email());
        validatePassword(userInput.password());

        GoogleSignUpResponse signUpResponse = firebaseAuthService.signUpUser(userInput);

        firebaseAuthService.setCustomUserClaims(signUpResponse.localId());
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(
                new RefreshTokenInput(signUpResponse.refreshToken()));

        return UserOutput.builder()
                .email(signUpResponse.email())
                .userId(refreshTokenResponse.user_id())
                .idToken(refreshTokenResponse.id_token())
                .refreshToken(refreshTokenResponse.refresh_token())
                .build();
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        validateEmail(userInput.email());

        GoogleSignInResponse signInResponse = firebaseAuthService.signInUser(userInput);

        return UserOutput.builder()
                .email(signInResponse.email())
                .userId(signInResponse.localId())
                .idToken(signInResponse.idToken())
                .refreshToken(signInResponse.refreshToken())
                .build();
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(refreshTokenInput);

        return RefreshTokenOutput.builder()
                .userId(refreshTokenResponse.user_id())
                .idToken(refreshTokenResponse.id_token())
                .refreshToken(refreshTokenResponse.refresh_token())
                .build();
    }

    private void validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new FirebaseAuthenticationException(INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new FirebaseAuthenticationException(INVALID_PASSWORD.getMessage());
        }
    }

}
