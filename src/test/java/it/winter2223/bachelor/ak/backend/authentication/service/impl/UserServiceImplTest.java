package it.winter2223.bachelor.ak.backend.authentication.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleRefreshTokenResponse;
import it.winter2223.bachelor.ak.backend.authentication.dto.google.GoogleSignUpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @MockBean
    FirebaseAuth firebaseAuth;
//    @Mock
//    private WebClient webClientMock;
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersMock;
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
//    @Mock
//    private WebClient.RequestBodySpec requestBodyMock;
//    @Mock
//    private WebClient.RequestBodyUriSpec requestBodyUriMock;
//    @Mock
//    private WebClient.ResponseSpec responseMock;
    @Mock
FirebaseAuthService firebaseAuthService;
    @InjectMocks
    UserServiceImpl underTest;

    @Test
    void shouldSignUpUser() {
        UserInput newUser = new UserInput("email@email.com", "password");
        GoogleSignUpResponse signUpResponse = new GoogleSignUpResponse(
                "idToken",
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

        when(firebaseAuthService.signUpUser(any(UserInput.class))).thenReturn(signUpResponse);
        when(firebaseAuthService.requestRefreshToken(any(RefreshTokenInput.class))).thenReturn(refreshTokenResponse);
        UserOutput userOutput = underTest.singUp(newUser);

        assertEquals(userOutput.email(), signUpResponse.email());
        assertEquals(userOutput.idToken(), refreshTokenResponse.id_token());
        assertEquals(userOutput.refreshToken(), refreshTokenResponse.refresh_token());
        verify(firebaseAuthService).signUpUser(newUser);
        verify(firebaseAuthService).setCustomUserClaims(signUpResponse);
        verify(firebaseAuthService).requestRefreshToken(new RefreshTokenInput(signUpResponse.refreshToken()));
    }
}
