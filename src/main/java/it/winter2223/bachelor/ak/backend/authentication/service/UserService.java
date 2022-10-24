package it.winter2223.bachelor.ak.backend.authentication.service;

import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.RefreshTokenOutput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserInput;
import it.winter2223.bachelor.ak.backend.authentication.dto.UserOutput;

public interface UserService {

    UserOutput singUp(UserInput userInput);

    UserOutput signIn(UserInput userInput);

    RefreshTokenOutput refreshToken(RefreshTokenInput refreshTokenInput);
}
