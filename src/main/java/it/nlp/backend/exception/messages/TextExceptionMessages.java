package it.nlp.backend.exception.messages;

public enum TextExceptionMessages {
    NO_TEXT_WITH_ENTERED_ID("There is no text with entered ID: "),
    TEXTS_NUMBER_OUT_OF_RANGE("Texts number needs to be a number between 1 and 100"),
    CANNOT_COMPARE_NULL_TEXT("Null text objects cannot be compared");

    private final String message;

    TextExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
