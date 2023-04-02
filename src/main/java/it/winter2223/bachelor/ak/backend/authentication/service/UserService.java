package it.winter2223.bachelor.ak.backend.authentication.service;

import it.winter2223.bachelor.ak.backend.authentication.dto.*;

public interface UserService {

    UserOutput signUp(UserInput userInput);

    UserOutput signIn(UserInput userInput);

    RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput);
}
