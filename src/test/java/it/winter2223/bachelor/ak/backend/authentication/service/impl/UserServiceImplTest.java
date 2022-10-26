package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @MockBean
    FirebaseAuth firebaseAuth;
    @Mock
    FirebaseAuthService firebaseAuthService;
    @InjectMocks
    UserServiceImpl underTest;

    @Test
    @DisplayName("signs up a user with entered email and password")
    void shouldSignUpUser() {
        UserInput userInput = new UserInput("email@email.com", "password");
        GoogleSignUpResponse signUpResponse = new GoogleSignUpResponse(
                "idToken",
                "email@email.com",
                "refreshToken",
                "expiresIn",
                "localId");
        GoogleRefreshTokenResponse refreshTokenResponse = new GoogleRefreshTokenResponse(
                "expires_in",
                "token_type",
                "refresh_token",
                "id_token",
                "user_id",
                "project_id");

        when(firebaseAuthService.signUpUser(any(UserInput.class))).thenReturn(signUpResponse);
        when(firebaseAuthService.requestRefreshToken(any(RefreshTokenInput.class))).thenReturn(refreshTokenResponse);
        UserOutput userOutput = underTest.singUp(userInput);

        assertEquals(userOutput.email(), signUpResponse.email());
        assertEquals(userOutput.idToken(), refreshTokenResponse.id_token());
        assertEquals(userOutput.refreshToken(), refreshTokenResponse.refresh_token());
        verify(firebaseAuthService).signUpUser(userInput);
        verify(firebaseAuthService).setCustomUserClaims(signUpResponse);
        verify(firebaseAuthService).requestRefreshToken(new RefreshTokenInput(signUpResponse.refreshToken()));
    }

    @Test
    @DisplayName("signs in a user with entered email and password")
    void shouldSignInUser() {
        UserInput userInput = new UserInput("email@email.com", "password");
        GoogleSignInResponse signInResponse = new GoogleSignInResponse(
                "idToken",
                "email@email.com",
                "refreshToken",
                "expiresIn",
                "localId",
                true);

        when(firebaseAuthService.signInUser(any(UserInput.class))).thenReturn(signInResponse);
        UserOutput userOutput = underTest.signIn(userInput);

        assertEquals(userOutput.email(), signInResponse.email());
        assertEquals(userOutput.idToken(), signInResponse.idToken());
        assertEquals(userOutput.refreshToken(), signInResponse.refreshToken());
        verify(firebaseAuthService).signInUser(userInput);
    }

    @Test
    @DisplayName("returns new idToken and refreshToken when valid refreshToken is passed")
    void shouldRefreshToken() {
        RefreshTokenInput refreshTokenInput = new RefreshTokenInput("refreshToken");
        GoogleRefreshTokenResponse refreshTokenResponse = new GoogleRefreshTokenResponse(
                "expires_in",
                "token_type",
                "refresh_Token",
                "id_token",
                "user_id",
                "project_id");

        when(firebaseAuthService.requestRefreshToken(any(RefreshTokenInput.class))).thenReturn(refreshTokenResponse);
        RefreshTokenOutput refreshTokenOutput = underTest.refreshToken(refreshTokenInput);

        assertEquals(refreshTokenOutput.idToken(), refreshTokenResponse.id_token());
        assertEquals(refreshTokenOutput.refreshToken(), refreshTokenOutput.refreshToken());
        verify(firebaseAuthService).requestRefreshToken(refreshTokenInput);
    }
}
