package it.winter2223.bachelor.ak.backend.comment.exception;

public enum CommentExceptionMessages {
    NO_COMMENT_WITH_ENTERED_ID("There is no comment with entered id"),
    CANNOT_COMPARE_NULL_COMMENT("Null comment object cannot be compare"),
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
