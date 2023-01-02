package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception;

public enum CommentEmotionAssignmentExceptionMessages {

    ASSIGNMENT_ALREADY_EXISTS("There already exists this user's assignment for this comment"),
    WRONG_EMOTION("Entered emotion does not exist"),
    FAILED_TO_OBTAIN_STREAM_WRITER("Failed to obtain the writer object for the character stream"),
    FAILED_TO_WRITE_CSV("Failed to write to csv file. ");

    private final String message;

    CommentEmotionAssignmentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
