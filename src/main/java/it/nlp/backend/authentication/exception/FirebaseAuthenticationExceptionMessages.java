package it.nlp.backend.authentication.exception;

public enum FirebaseAuthenticationExceptionMessages {
    INVALID_EMAIL_ADDRESS("Email address is invalid"),
    INVALID_PASSWORD("Password must be at least 6 characters"),
    SIGNING_UP_FAILED("Failed to sign up"),
    SIGNING_IN_FAILED("Failed to sign in"),
    TOKEN_REFRESHING_FAILED("Failed to refresh token"),
    SETTING_USER_CLAIMS_FAILED("Failed to set user claims"),
    NO_USER_WITH_PASSED_ID("There is no user with entered id");

    private final String message;

    FirebaseAuthenticationExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
