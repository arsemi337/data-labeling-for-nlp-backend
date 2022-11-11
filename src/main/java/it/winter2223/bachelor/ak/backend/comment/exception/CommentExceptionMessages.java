package it.winter2223.bachelor.ak.backend.comment.exception;

public enum CommentExceptionMessages {
    VIDEOS_FETCHING_ERROR("Error while fetching You Tube videos list"),
    COMMENTS_FETCHING_ERROR("Error while fetching You Tube comments list");

    private final String message;

    CommentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
