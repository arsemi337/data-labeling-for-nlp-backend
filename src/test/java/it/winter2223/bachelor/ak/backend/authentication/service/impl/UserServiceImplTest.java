package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignInResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.model.User;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @MockBean
    FirebaseAuth firebaseAuth;
    @Mock
    UserRepository userRepository;
    @Mock
    FirebaseAuthService firebaseAuthService;
    @InjectMocks
    UserServiceImpl underTest;

    @Test
    @DisplayName("signs up a user with entered email and password")
    void shouldSignUpUser() {
        UserInput userInput = new UserInput("email@email.com", "password");
        GoogleSignUpResponse signUpResponse = new GoogleSignUpResponse(
                "accessToken",
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
        User user = User.builder().userId("userId").build();

        when(firebaseAuthService.signUpUser(any(UserInput.class))).thenReturn(signUpResponse);
        doNothing().when(firebaseAuthService).setCustomUserClaims(anyString());
        when(firebaseAuthService.requestRefreshToken(any(RefreshTokenInput.class))).thenReturn(refreshTokenResponse);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserOutput userOutput = underTest.signUp(userInput);

        assertEquals(userOutput.email(), signUpResponse.email());
        assertEquals(userOutput.userId(), refreshTokenResponse.user_id());
        assertEquals(userOutput.accessToken(), refreshTokenResponse.id_token());
        assertEquals(userOutput.expiresIn(), refreshTokenResponse.expires_in());
        assertEquals(userOutput.refreshToken(), refreshTokenResponse.refresh_token());
        verify(firebaseAuthService).signUpUser(userInput);
        verify(firebaseAuthService).setCustomUserClaims(signUpResponse.localId());
        verify(firebaseAuthService).requestRefreshToken(new RefreshTokenInput(signUpResponse.refreshToken()));
        verify(userRepository).save(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"test.pl", "test@test", "test@test.", "@test.pl"})
    @DisplayName("when invalid email address is passed while signing up, FirebaseAuthenticationException " +
            "with INVALID_EMAIL_ADDRESS message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenSigningUpWithInvalidEmailAddress(String email) {
        UserInput userInput = UserInput.builder()
                        .email(email)
                        .password("password")
                        .build();
        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.signUp(userInput)).withMessage(INVALID_EMAIL_ADDRESS.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"a", "ab", "abc", "abcd", "abcde"})
    @DisplayName("when invalid password is passed while signing up, FirebaseAuthenticationException " +
            "with INVALID_PASSWORD message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenSigningUpWithInvalidPassword(String password) {
        UserInput userInput = UserInput.builder()
                .email("test@test.pl")
                .password(password)
                .build();
        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.signUp(userInput)).withMessage(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("when system fails to sign up a user on Firebase Auth, FirebaseAuthenticationException " +
            "with SIGNING_UP_FAILED message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenFailsToSignUpUser() {
        UserInput userInput = UserInput.builder()
                .email("test@test.pl")
                .password("password")
                .build();
        when(firebaseAuthService.signUpUser(userInput))
                .thenThrow(new FirebaseAuthenticationException(SIGNING_UP_FAILED.getMessage()));

        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.signUp(userInput)).withMessage(SIGNING_UP_FAILED.getMessage());

        verify(firebaseAuthService, never()).setCustomUserClaims(anyString());
        verify(firebaseAuthService, never()).requestRefreshToken(any(RefreshTokenInput.class));
    }


    @Test
    @DisplayName("signs in a user with entered email and password")
    void shouldSignInUser() {
        UserInput userInput = new UserInput("email@email.com", "password");
        GoogleSignInResponse signInResponse = new GoogleSignInResponse(
                "accessToken",
                "email@email.com",
                "refreshToken",
                "expiresIn",
                "localId",
                true);

        when(firebaseAuthService.signInUser(any(UserInput.class))).thenReturn(signInResponse);
        UserOutput userOutput = underTest.signIn(userInput);

        assertEquals(userOutput.email(), signInResponse.email());
        assertEquals(userOutput.userId(), signInResponse.localId());
        assertEquals(userOutput.accessToken(), signInResponse.idToken());
        assertEquals(userOutput.expiresIn(), signInResponse.expiresIn());
        assertEquals(userOutput.refreshToken(), signInResponse.refreshToken());
        verify(firebaseAuthService).signInUser(userInput);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"test.pl", "test@test", "test@test.", "@test.pl"})
    @DisplayName("when invalid email address is passed while signing in, FirebaseAuthenticationException " +
            "with INVALID_EMAIL_ADDRESS message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenSigningInWithInvalidEmailAddress(String email) {
        UserInput userInput = UserInput.builder()
                .email(email)
                .password("password")
                .build();
        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.signIn(userInput)).withMessage(INVALID_EMAIL_ADDRESS.getMessage());
    }

    @Test
    @DisplayName("when system fails to sign in a user on Firebase Auth, FirebaseAuthenticationException " +
            "with SIGNING_IN_FAILED message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenFailsToSignInUser() {
        UserInput userInput = UserInput.builder()
                .email("test@test.pl")
                .password("password")
                .build();
        when(firebaseAuthService.signInUser(userInput))
                .thenThrow(new FirebaseAuthenticationException(SIGNING_IN_FAILED.getMessage()));

        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.signIn(userInput)).withMessage(SIGNING_IN_FAILED.getMessage());
    }

    @Test
    @DisplayName("returns new accessToken and refreshToken when valid refreshToken is passed")
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

        assertEquals(refreshTokenOutput.userId(), refreshTokenResponse.user_id());
        assertEquals(refreshTokenOutput.idToken(), refreshTokenResponse.id_token());
        assertEquals(refreshTokenOutput.expiresIn(), refreshTokenResponse.expires_in());
        assertEquals(refreshTokenOutput.refreshToken(), refreshTokenOutput.refreshToken());
        verify(firebaseAuthService).requestRefreshToken(refreshTokenInput);
    }

    @Test
    @DisplayName("when system fails to refresh token on Firebase Auth, FirebaseAuthenticationException " +
            "with TOKEN_REFRESHING_FAILED message should be thrown")
    void shouldThrowFirebaseAuthenticationExceptionWhenFailsToRefreshToken() {
        RefreshTokenInput refreshTokenInput = RefreshTokenInput.builder()
                        .refreshToken("refreshToken")
                        .build();
        when(firebaseAuthService.requestRefreshToken(refreshTokenInput))
                .thenThrow(new FirebaseAuthenticationException(TOKEN_REFRESHING_FAILED.getMessage()));

        assertThatExceptionOfType(FirebaseAuthenticationException.class)
                .isThrownBy(() -> underTest.refreshToken(refreshTokenInput)).withMessage(TOKEN_REFRESHING_FAILED.getMessage());
    }
}
