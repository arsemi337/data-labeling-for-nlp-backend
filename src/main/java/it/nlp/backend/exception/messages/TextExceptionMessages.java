package it.nlp.backend.exception.messages;

public enum TextExceptionMessages {
    NO_COMMENT_WITH_ENTERED_ID("There is no comment with entered id: "),
    COMMENTS_NUMBER_OUT_OF_RANGE("Comments number needs to be a number between 1 and 100"),
    CANNOT_COMPARE_NULL_COMMENT("Null comment object cannot be compared");

    private final String message;

    TextExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
