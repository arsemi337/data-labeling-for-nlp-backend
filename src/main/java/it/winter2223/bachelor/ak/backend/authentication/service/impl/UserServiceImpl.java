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

        var user = User.builder()
                .email(userInput.email())
                .password(passwordEncoder.encode(userInput.password()))
                .userRole(UserRole.USER)
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return UserOutput.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .accessToken(jwtToken)
                .expiresIn("360000000000000")
                .refreshToken("")
                .build();
    }

    @Override
    public UserOutput signIn(UserInput userInput) {
        validateEmail(userInput.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userInput.email(),
                        userInput.password()
                )
        );

        var user = userRepository.findByEmail(userInput.email())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return UserOutput.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .accessToken(jwtToken)
                .expiresIn("360000000000000")
                .refreshToken("")
                .build();
    }

    // TODO: Zrobić to
    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        // refreshTokenInput.refreshToken()

        return RefreshTokenOutput.builder()
                .userId("")
                .idToken("")
                .expiresIn("")
                .refreshToken("")
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
