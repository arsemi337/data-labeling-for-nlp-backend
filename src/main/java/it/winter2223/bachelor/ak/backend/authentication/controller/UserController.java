package it.winter2223.bachelor.ak.backend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.authentication.dto.*;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Change status code from 403 to 401
@RestController
@Tag(name = "Authentication")
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/token")
    @Operation(summary = "Refresh token")
    ResponseEntity<RefreshTokenOutput> refreshToken(@RequestBody RefreshTokenInput refreshTokenInput) {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenInput));
    }

    @PostMapping("/register")
    @Operation(summary = "Sign up")
    public ResponseEntity<UserOutput> register(@RequestBody UserInput userInput) {
        return ResponseEntity.ok(userService.signUp(userInput));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Sign in")
    public ResponseEntity<UserOutput> authenticate(@RequestBody UserInput userInput) {
        return ResponseEntity.ok(userService.signIn(userInput));
    }
}
