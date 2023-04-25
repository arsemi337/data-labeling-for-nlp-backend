package it.nlp.backend.authentication.controller;

import it.nlp.backend.authentication.dto.*;
import it.nlp.backend.authentication.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("when correct body is passed, a user should be signed up")
    void shouldSignUp() throws Exception {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        UserRoleOutput role = UserRoleOutput.USER;
        TokenOutput tokenOutput = TokenOutput.builder()
                .value("token")
                .expiresIn(1000L)
                .build();
        UserOutput mockedValue = new UserOutput(userId, userEmail, role, tokenOutput, tokenOutput);

        when(userService.signUp(any(UserInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(getUserInputBody()))
                .andExpect(jsonPath("$.userId", equalTo(userId.toString())))
                .andExpect(jsonPath("$.email", equalTo(userEmail)))
                .andExpect(jsonPath("$.userRoleOutput", equalTo(role.name())))
                .andExpect(jsonPath("$.accessTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.accessTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())))
                .andExpect(jsonPath("$.refreshTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.refreshTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())));
        verify(userService).signUp(any(UserInput.class));
    }

    @Test
    @DisplayName("when correct body is passed, a user should be signed in")
    void shouldSignIn() throws Exception {
        UUID userId = UUID.randomUUID();
        String userEmail = "email@email.com";
        UserRoleOutput role = UserRoleOutput.USER;
        TokenOutput tokenOutput = TokenOutput.builder()
                .value("token")
                .expiresIn(1000L)
                .build();
        UserOutput mockedValue = new UserOutput(userId, userEmail, role, tokenOutput, tokenOutput);

        when(userService.signIn(any(UserInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType("application/json")
                        .content(getUserInputBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.userId", equalTo(userId.toString())))
                .andExpect(jsonPath("$.email", equalTo(userEmail)))
                .andExpect(jsonPath("$.userRoleOutput", equalTo(role.name())))
                .andExpect(jsonPath("$.accessTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.accessTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())))
                .andExpect(jsonPath("$.refreshTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.refreshTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())));
        verify(userService).signIn(any(UserInput.class));
    }

    @Test
    @DisplayName("when correct body is passed, a refreshed token should be returned")
    void shouldReturnRefreshedToken() throws Exception {
        UUID userId = UUID.randomUUID();
        TokenOutput tokenOutput = TokenOutput.builder()
                .value("token")
                .expiresIn(1000L)
                .build();
        RefreshTokenOutput mockedValue = new RefreshTokenOutput(userId, tokenOutput, tokenOutput);

        when(userService.refreshToken(any(RefreshTokenInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(getRefreshTokenInputBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.userId", equalTo(userId.toString())))
                .andExpect(jsonPath("$.accessTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.accessTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())))
                .andExpect(jsonPath("$.refreshTokenOutput.value", equalTo(tokenOutput.value())))
                .andExpect(jsonPath("$.refreshTokenOutput.expiresIn", equalTo(tokenOutput.expiresIn().intValue())));
        verify(userService).refreshToken(any(RefreshTokenInput.class));
    }

    private String getUserInputBody() {
        return """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
               """;
    }

    private String getRefreshTokenInputBody() {
        return """
                {
                    "refreshTokenValue": "refreshToken"
                }
               """;
    }
}
