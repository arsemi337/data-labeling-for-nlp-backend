package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.persistence.User;
import it.winter2223.bachelor.ak.backend.authentication.persistence.UserRole;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import it.winter2223.bachelor.ak.backend.config.security.JwtService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_EMAIL_ADDRESS;
import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_PASSWORD;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserOutput signUp(UserInput userInput) {
        validateEmail(userInput.email());
        validatePassword(userInput.password());

        GoogleSignUpResponse signUpResponse = firebaseAuthService.signUpUser(userInput);

        firebaseAuthService.setCustomUserClaims(signUpResponse.localId());
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(
                new RefreshTokenInput(signUpResponse.refreshToken()));

        userRepository.save(
                User.builder()
                .userId(refreshTokenResponse.user_id())
                .build());

        return UserOutput.builder()
                .email(signUpResponse.email())
                .userId(refreshTokenResponse.user_id())
                .idToken(refreshTokenResponse.id_token())
                .expiresIn(refreshTokenResponse.expires_in())
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
                .expiresIn(signInResponse.expiresIn())
                .refreshToken(signInResponse.refreshToken())
                .build();
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        GoogleRefreshTokenResponse refreshTokenResponse = firebaseAuthService.requestRefreshToken(refreshTokenInput);

        return RefreshTokenOutput.builder()
                .userId(refreshTokenResponse.user_id())
                .idToken(refreshTokenResponse.id_token())
                .expiresIn(refreshTokenResponse.expires_in())
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
