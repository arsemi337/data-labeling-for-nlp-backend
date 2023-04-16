package it.nlp.backend.exception.messages;

public enum TextEmotionAssignmentExceptionMessages {
    ASSIGNMENT_ALREADY_EXISTS("There already exists this user's assignment for this comment"),
    WRONG_EMOTION("Entered emotion does not exist"),
    FAILED_TO_WRITE_CSV("Failed to write to csv file");

    private final String message;

    TextEmotionAssignmentExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
