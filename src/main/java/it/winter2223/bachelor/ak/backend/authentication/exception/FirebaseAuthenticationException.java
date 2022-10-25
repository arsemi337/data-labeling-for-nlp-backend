package it.winter2223.bachelor.ak.backend.authentication.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class FirebaseAuthenticationException extends BusinessLogicException {

    public FirebaseAuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}