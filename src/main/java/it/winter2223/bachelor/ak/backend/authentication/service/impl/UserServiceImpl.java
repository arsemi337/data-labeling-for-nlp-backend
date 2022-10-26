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

        return new UserOutput(
                signUpResponse.email(),
                refreshTokenResponse.id_token(),
                refreshTokenResponse.refresh_token());
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        GoogleSignInResponse signInResponse = firebaseAuthService.signInUser(userInput);

        return new UserOutput(signInResponse.email(), signInResponse.idToken(), signInResponse.refreshToken());
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(refreshTokenInput);

        return new RefreshTokenOutput(refreshTokenResponse.id_token(), refreshTokenResponse.refresh_token());
    }





}
