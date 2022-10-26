package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final FirebaseAuthService firebaseAuthService;


    public UserServiceImpl(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @Override
    public UserOutput singUp(UserInput userInput) {
        GoogleSignUpResponse signUpResponse = firebaseAuthService.signUpUser(userInput);

        firebaseAuthService.setCustomUserClaims(signUpResponse);
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(
                new RefreshTokenInput(signUpResponse.refreshToken()));

        return UserOutput.builder()
                .email(signUpResponse.email())
                .idToken(refreshTokenResponse.id_token())
                .refreshToken(refreshTokenResponse.refresh_token())
                .build();
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        GoogleSignInResponse signInResponse = firebaseAuthService.signInUser(userInput);

        return UserOutput.builder()
                .email(signInResponse.email())
                .idToken(signInResponse.idToken())
                .refreshToken(signInResponse.refreshToken())
                .build();
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(refreshTokenInput);

        return RefreshTokenOutput.builder()
                .idToken(refreshTokenResponse.id_token())
                .refreshToken(refreshTokenResponse.refresh_token())
                .build();
    }





}
