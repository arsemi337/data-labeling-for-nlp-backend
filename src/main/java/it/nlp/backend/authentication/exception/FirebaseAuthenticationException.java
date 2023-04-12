package it.nlp.backend.authentication.exception;

import it.nlp.backend.exception.BusinessLogicException;

public class FirebaseAuthenticationException extends BusinessLogicException {

    public FirebaseAuthenticationException(String errorMessage) {
        super(errorMessage);
    }
    public FirebaseAuthenticationException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}