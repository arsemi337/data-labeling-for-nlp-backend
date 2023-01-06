package it.winter2223.bachelor.ak.backend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication")
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    @Operation(summary = "Sign up")
    ResponseEntity<UserOutput> signUp(@RequestBody UserInput userInput) {
        return ResponseEntity.ok(userService.singUp(userInput));
    }

    @PostMapping("/user")
    @Operation(summary = "Sign in")
    ResponseEntity<UserOutput> signIn(@RequestBody UserInput userInput) {
        return ResponseEntity.ok(userService.signIn(userInput));
    }

    @PostMapping("/token")
    @Operation(summary = "Refresh token")
    ResponseEntity<RefreshTokenOutput> refreshToken(@RequestBody RefreshTokenInput refreshTokenInput) {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenInput));
    }
}
