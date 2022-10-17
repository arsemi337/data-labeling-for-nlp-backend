package it.winter2223.bachelor.ak.backend.comments.exception;

public enum CommentsExceptionMessages {

    INVALID_WHATEVER("Something is invalid");

    private final String message;

    CommentsExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
