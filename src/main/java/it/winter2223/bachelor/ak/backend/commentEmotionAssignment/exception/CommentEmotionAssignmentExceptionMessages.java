package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception;

public enum CommentEmotionAssignmentExceptionMessages {

    COMMENT_NOT_FOUND("Comment with entered ID does not exist"),
    WRONG_EMOTION("Entered emotion does not exist");

    private final String message;

    CommentEmotionAssignmentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
