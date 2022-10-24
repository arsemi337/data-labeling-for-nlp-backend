package it.winter2223.bachelor.ak.backend.authentication.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class AuthenticationException extends BusinessLogicException {

    public AuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}