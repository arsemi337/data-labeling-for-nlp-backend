package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.model.User;
import it.winter2223.bachelor.ak.backend.authentication.model.UserRole;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import it.winter2223.bachelor.ak.backend.config.security.JwtService;
import it.winter2223.bachelor.ak.backend.utils.TimeSupplier;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_EMAIL_ADDRESS;
import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.INVALID_PASSWORD;


// TODO: TOKENS!!!
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

        var user = User.builder()
                .userId(UUID.randomUUID())
                .createdAt(timeSupplier.get())
                .email(userInput.email())
                .password(passwordEncoder.encode(userInput.password()))
                .userRole(UserRole.USER)
                .assignedEmotionTextIds(new ArrayList<>())
                .build();
        user = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return userMapper.mapToUserOutput(user, jwtToken);
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

        return userMapper.mapToUserOutput(user, jwtToken);
    }

    // TODO: Zrobić to
    @Override
    public RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput) {
        // refreshTokenInput.refreshToken()

        return RefreshTokenOutput.builder()
                .userId("randomId")
                .accessToken("")
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
