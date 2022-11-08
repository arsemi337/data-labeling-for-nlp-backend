package it.winter2223.bachelor.ak.backend.comment.exception;

public enum CommentExceptionMessages {
    INVALID_EMAIL_ADDRESS("Email address is invalid");

    private final String message;

    CommentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}