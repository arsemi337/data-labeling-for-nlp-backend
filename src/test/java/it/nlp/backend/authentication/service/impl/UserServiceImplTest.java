package it.nlp.backend.authentication.service.impl;

import it.nlp.backend.authentication.dto.*;
import it.nlp.backend.authentication.model.User;
import it.nlp.backend.authentication.model.UserRole;
import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.utils.TimeSupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static it.nlp.backend.exception.messages.SecurityExceptionMessages.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtService jwtService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TimeSupplier timeSupplier;
    @InjectMocks
    UserServiceImpl underTest;
    public long ACCESS_TOKEN_EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7;
    public long REFRESH_TOKEN_EXPIRY_TIME = 1000L * 60 * 60 * 24 * 30;

    @Test
    @DisplayName("signs up a user with entered email and password")
    void shouldSignUpUser() {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        UserRoleOutput role = UserRoleOutput.USER;
        TokenOutput accessTokenOutput = getAccessTokenOutput();
        TokenOutput refreshTokenOutput = getRefreshTokenOutput();
        String userPassword = "password";
        UserInput userInput = new UserInput(userEmail, "password");
        User user = getUser(userId, userEmail);

        String token = "token";
        when(userRepository.existsByEmail(userEmail)).thenReturn(false);
        when(jwtService.generateAccessToken(userEmail)).thenReturn(token);
        when(jwtService.generateRefreshToken(userEmail)).thenReturn(token);
        when(passwordEncoder.encode(userPassword)).thenReturn(userPassword);
        when(timeSupplier.get()).thenReturn(LocalDateTime.of(2000, 10, 5, 10, 22, 5));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserOutput userOutput = underTest.signUp(userInput);

        assertEquals(userOutput.userId(), userId);
        assertEquals(userOutput.email(), userEmail);
        assertEquals(userOutput.userRoleOutput(), role);
        assertEquals(userOutput.accessTokenOutput().value(), accessTokenOutput.value());
        assertEquals(userOutput.accessTokenOutput().expiresIn(), accessTokenOutput.expiresIn());
        assertEquals(userOutput.refreshTokenOutput().value(), refreshTokenOutput.value());
        assertEquals(userOutput.refreshTokenOutput().expiresIn(), refreshTokenOutput.expiresIn());
        verify(jwtService).generateAccessToken(userEmail);
        verify(jwtService).generateRefreshToken(userEmail);
        verify(timeSupplier).get();
        verify(userRepository).save(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"test.pl", "test@test", "test@test.", "@test.pl"})
    @DisplayName("when invalid email address is passed while signing up, IllegalArgumentException " +
            "with INVALID_EMAIL_ADDRESS message should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenSigningUpWithInvalidEmailAddress(String email) {
        UserInput userInput = getUserInput(email, "password");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.signUp(userInput)).withMessage(INVALID_EMAIL_ADDRESS.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"a", "ab", "abc", "abcd", "abcde"})
    @DisplayName("when invalid password is passed while signing up, IllegalArgumentException " +
            "with INVALID_PASSWORD message should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenSigningUpWithInvalidPassword(String password) {
        UserInput userInput = getUserInput("test@test.pl", password);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.signUp(userInput)).withMessage(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("when user trying to sign up has an existing account, IllegalArgumentException " +
            "with EMAIL_ALREADY_TAKEN message should be thrown")
    void shouldThrowIllegalArgumentExceptionnWhenFailsToSignUpUser() {
        String userEmail = "email@email.com";
        UserInput userInput = getUserInput(userEmail, "password");
        when(userRepository.existsByEmail(userEmail))
                .thenReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.signUp(userInput))
                .withMessage(EMAIL_ALREADY_TAKEN.getMessage() + userEmail);

        verify(jwtService, never()).generateAccessToken(userEmail);
        verify(jwtService, never()).generateRefreshToken(userEmail);
        verify(timeSupplier, never()).get();
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("signs in a user with entered email and password")
    void shouldSignInUser() {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        UserRoleOutput role = UserRoleOutput.USER;
        TokenOutput accessTokenOutput = getAccessTokenOutput();
        TokenOutput refreshTokenOutput = getRefreshTokenOutput();
        String userPassword = "password";
        UserInput userInput = new UserInput(userEmail, "password");
        User user = getUser(userId, userEmail);
        UsernamePasswordAuthenticationToken authToken = getUsernamePasswordAuthenticationToken(userEmail, userPassword);

        String token = "token";
        when(authenticationManager.authenticate(authToken)).thenReturn(null);
        when(jwtService.generateAccessToken(userEmail)).thenReturn(token);
        when(jwtService.generateRefreshToken(userEmail)).thenReturn(token);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenAnswer(answer -> answer.getArgument(0));
        UserOutput userOutput = underTest.signIn(userInput);

        assertEquals(userOutput.userId(), userId);
        assertEquals(userOutput.email(), userEmail);
        assertEquals(userOutput.userRoleOutput(), role);
        assertEquals(userOutput.accessTokenOutput().value(), accessTokenOutput.value());
        assertEquals(userOutput.accessTokenOutput().expiresIn(), accessTokenOutput.expiresIn());
        assertEquals(userOutput.refreshTokenOutput().value(), refreshTokenOutput.value());
        assertEquals(userOutput.refreshTokenOutput().expiresIn(), refreshTokenOutput.expiresIn());
        verify(jwtService).generateAccessToken(userEmail);
        verify(jwtService).generateRefreshToken(userEmail);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).save(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"test.pl", "test@test", "test@test.", "@test.pl"})
    @DisplayName("when invalid email address is passed while signing in, IllegalArgumentException " +
            "with INVALID_EMAIL_ADDRESS message should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenSigningInWithInvalidEmailAddress(String email) {
        UserInput userInput = getUserInput(email, "password");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.signIn(userInput)).withMessage(INVALID_EMAIL_ADDRESS.getMessage());
    }

    @Test
    @DisplayName("when user tries to sign in with bad credentials, IllegalArgumentException " +
            "with BAD_CREDENTIALS message should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenFailsToSignInUser() {
        String userEmail = "email@email.com";
        String userPassword = "password";
        UserInput userInput = getUserInput(userEmail, userPassword);
        UsernamePasswordAuthenticationToken authToken = getUsernamePasswordAuthenticationToken(userEmail, userPassword);
        when(authenticationManager.authenticate(authToken))
                .thenThrow(new BadCredentialsException(""));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.signIn(userInput)).withMessage(BAD_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("returns new accessTokenOutput and refreshTokenOutput when valid refreshTokenOutput is passed")
    void shouldRefreshToken() {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        String refreshToken = "refreshTokenInput";
        RefreshTokenInput refreshTokenInput = getRefreshTokenInput(refreshToken);
        User user = getUser(userId, userEmail);
        TokenOutput accessTokenOutput = getAccessTokenOutput();
        TokenOutput refTokenOutput = getRefreshTokenOutput();

        String token = "token";
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(userEmail)).thenReturn(token);
        when(jwtService.generateRefreshToken(userEmail)).thenReturn(token);
        when(userRepository.save(user)).thenAnswer(answer -> answer.getArgument(0));
        RefreshTokenOutput refreshTokenOutput = underTest.refreshToken(refreshTokenInput);

        assertEquals(refreshTokenOutput.userId(), userId);
        assertEquals(refreshTokenOutput.accessTokenOutput().value(), accessTokenOutput.value());
        assertEquals(refreshTokenOutput.accessTokenOutput().expiresIn(), accessTokenOutput.expiresIn());
        assertEquals(refreshTokenOutput.refreshTokenOutput().value(), refTokenOutput.value());
        assertEquals(refreshTokenOutput.refreshTokenOutput().expiresIn(), refTokenOutput.expiresIn());

        verify(userRepository).findByRefreshToken(refreshToken);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService).generateAccessToken(userEmail);
        verify(jwtService).generateRefreshToken(userEmail);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("when entered refresh token does not exist, NoSuchElementException " +
            "with TOKEN_DOES_NOT_EXIST message should be thrown")
    void shouldThrowNoSuchElementExceptionWhenRefreshTokenDoesNotExist() {
        String refreshToken = "refreshTokenInput";
        RefreshTokenInput refreshTokenInput = getRefreshTokenInput(refreshToken);

        when(userRepository.findByRefreshToken(refreshToken))
                .thenThrow(new NoSuchElementException(TOKEN_DOES_NOT_EXIST.getMessage()));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> underTest.refreshToken(refreshTokenInput)).withMessage(TOKEN_DOES_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("when entered refresh token is invalid, IllegalArgumentException " +
            "with INVALID_REFRESH_TOKEN message should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenRefreshTokenIsInvalid() {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        String refreshToken = "refreshTokenInput";
        RefreshTokenInput refreshTokenInput = getRefreshTokenInput(refreshToken);
        User user = getUser(userId, userEmail);

        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.refreshToken(refreshTokenInput))
                .withMessage(INVALID_REFRESH_TOKEN.getMessage() + refreshToken);
    }

    private UserInput getUserInput(String userEmail, String userPassword) {
        return UserInput.builder()
                .email(userEmail)
                .password(userPassword)
                .build();
    }

    private RefreshTokenInput getRefreshTokenInput(String refreshToken) {
        return RefreshTokenInput.builder()
                .refreshTokenValue(refreshToken)
                .build();
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String userEmail, String userPassword) {
        return new UsernamePasswordAuthenticationToken(
                userEmail,
                userPassword);
    }

    private User getUser(UUID userId, String userEmail) {
        return User.builder()
                .userId(userId)
                .email(userEmail)
                .userRole(UserRole.USER)
                .build();
    }

    private TokenOutput getRefreshTokenOutput() {
        return TokenOutput.builder()
                .value("token")
                .expiresIn(REFRESH_TOKEN_EXPIRY_TIME)
                .build();
    }

    private TokenOutput getAccessTokenOutput() {
        return TokenOutput.builder()
                .value("token")
                .expiresIn(ACCESS_TOKEN_EXPIRY_TIME)
                .build();
    }
}
