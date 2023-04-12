package it.nlp.backend.authentication.service.impl;

import it.nlp.backend.authentication.dto.RefreshTokenInput;
import it.nlp.backend.authentication.dto.RefreshTokenOutput;
import it.nlp.backend.authentication.dto.UserInput;
import it.nlp.backend.authentication.dto.UserOutput;
import it.nlp.backend.authentication.exception.FirebaseAuthenticationException;
import it.nlp.backend.authentication.exception.FirebaseAuthenticationExceptionMessages;
import it.nlp.backend.authentication.model.User;
import it.nlp.backend.authentication.model.UserRole;
import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.authentication.service.UserService;
import it.nlp.backend.config.security.JwtService;
import it.nlp.backend.utils.TimeSupplier;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TimeSupplier timeSupplier;
    private final UserMapper userMapper;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TimeSupplier timeSupplier) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.timeSupplier = timeSupplier;
        this.userMapper = new UserMapper();
    }

    @Override
    public UserOutput signUp(UserInput userInput) {
        validateEmail(userInput.email());
        validatePassword(userInput.password());

        var jwtRefreshToken = jwtService.generateRefreshToken(userInput.email());
        var jwtAccessToken = jwtService.generateAccessToken(userInput.email());

        var user = User.builder()
                .userId(UUID.randomUUID())
                .createdAt(timeSupplier.get())
                .email(userInput.email())
                .password(passwordEncoder.encode(userInput.password()))
                .userRole(UserRole.USER)
                .assignedEmotionTextIds(new ArrayList<>())
                .refreshToken(jwtRefreshToken)
                .build();

        user = userRepository.save(user);

        return userMapper.mapToUserOutput(user, jwtAccessToken, jwtRefreshToken);
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

        var jwtRefreshToken = jwtService.generateRefreshToken(userInput.email());
        var jwtAccessToken = jwtService.generateAccessToken(userInput.email());

        var user = userRepository.findByEmail(userInput.email())
                .orElseThrow();

        user.setRefreshToken(jwtRefreshToken);
        userRepository.save(user);


        return userMapper.mapToUserOutput(user, jwtAccessToken, jwtRefreshToken);
    }

    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        var refreshToken = refreshTokenInput.refreshTokenValue();

        // TODO: exception - invalid refresh token
        var user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow();

        // TODO: exception 2 - token expired
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException();
        }

        var jwtRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        var jwtAccessToken = jwtService.generateAccessToken(user.getUsername());

        user.setRefreshToken(jwtRefreshToken);
        userRepository.save(user);

        return userMapper.mapToRefreshTokenOutput(
                user.getUserId().toString(),
                jwtAccessToken,
                jwtRefreshToken);
    }

    private void validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new FirebaseAuthenticationException(FirebaseAuthenticationExceptionMessages.INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new FirebaseAuthenticationException(FirebaseAuthenticationExceptionMessages.INVALID_PASSWORD.getMessage());
        }
    }
}
