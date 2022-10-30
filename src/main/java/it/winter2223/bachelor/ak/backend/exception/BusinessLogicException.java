package it.winter2223.bachelor.ak.backend.exception;

public abstract class BusinessLogicException extends RuntimeException {

    protected BusinessLogicException(String errorMessage) {
        super(errorMessage);
    }
    protected BusinessLogicException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
