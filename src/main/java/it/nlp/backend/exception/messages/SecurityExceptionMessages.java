package it.nlp.backend.exception.messages;

public enum SecurityExceptionMessages {
    INVALID_EMAIL_ADDRESS("Email address is invalid"),
    INVALID_PASSWORD("Password must be at least 6 characters"),
    NO_USER_WITH_PASSED_ID("There is no user with entered id: "),
    TOKEN_DOES_NOT_EXIST("Entered token does not exist"),
    INVALID_REFRESH_TOKEN("Entered refresh token is invalid");

    private final String message;

    SecurityExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
