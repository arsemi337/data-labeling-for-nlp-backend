package it.winter2223.bachelor.ak.backend.authentication.controller;

import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
        UserOutput mockedValue = new UserOutput("email@email.com", "idToken", "refreshToken");

        when(userService.singUp(any(UserInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth")
                        .contentType("application/json")
                        .content(getUserInputBody()))
                .andExpect(jsonPath("$.email", equalTo("email@email.com")))
                .andExpect(jsonPath("$.idToken", equalTo("idToken")))
                .andExpect(jsonPath("$.refreshToken", equalTo("refreshToken")));
        verify(userService).singUp(any(UserInput.class));
    }

    @Test
    @DisplayName("when correct body is passed, a user should be signed in")
    void shouldSignIn() throws Exception {
        UserOutput mockedValue = new UserOutput("email@email.com", "idToken", "refreshToken");

        when(userService.signIn(any(UserInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/user")
                        .contentType("application/json")
                        .content(getUserInputBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.email", equalTo("email@email.com")))
                .andExpect(jsonPath("$.idToken", equalTo("idToken")))
                .andExpect(jsonPath("$.refreshToken", equalTo("refreshToken")));
        verify(userService).signIn(any(UserInput.class));
    }

    @Test
    @DisplayName("when correct body is passed, a refreshed token should be returned")
    void shouldReturnRefreshedToken() throws Exception {
        RefreshTokenOutput mockedValue = new RefreshTokenOutput("idToken", "refreshToken");

        when(userService.refreshToken(any(RefreshTokenInput.class))).thenReturn(mockedValue);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(getRefreshTokenInputBody()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.idToken", equalTo("idToken")))
                .andExpect(jsonPath("$.refreshToken", equalTo("refreshToken")));
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
                    "refreshToken": "refreshToken",
                }
               """;
    }
}
