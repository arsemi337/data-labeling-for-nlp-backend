package it.winter2223.bachelor.ak.backend.comment.exception;

public enum CommentExceptionMessages {
    NO_COMMENT_WITH_ENTERED_ID("There is no comment with entered id"),
    COMMENTS_NUMBER_IS_NOT_INTEGER("Comments number is not an integer value"),
    COMMENTS_NUMBER_OUT_OF_RANGE("Comments number needs to be a number between 1 and 100"),
    CANNOT_COMPARE_NULL_COMMENT("Null comment object cannot be compared"),
    VIDEOS_FETCHING_ERROR("Error while fetching YouTube videos list"),
    COMMENTS_FETCHING_ERROR("Error while fetching You Tube comments list of video with id: ");

    private final String message;

    CommentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
