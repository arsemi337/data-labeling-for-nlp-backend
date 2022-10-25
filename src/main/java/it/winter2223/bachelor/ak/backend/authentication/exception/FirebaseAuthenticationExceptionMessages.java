package it.winter2223.bachelor.ak.backend.authentication.exception;

public enum FirebaseAuthenticationExceptionMessages {
    SOMETHING_WENT_WRONG("Something went wrong");

    private final String message;

    FirebaseAuthenticationExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
