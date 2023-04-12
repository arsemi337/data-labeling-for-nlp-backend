package it.nlp.backend.exception;

public abstract class BusinessLogicException extends RuntimeException {

    protected BusinessLogicException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
