package it.nlp.backend.exception.messages;

public enum TextAnalysisExceptionMessages {
    INPUT_TEXT_EMPTY("Entered text input cannot contain empty fields"),
    UNEXPECTED_ERROR("An unexpected error has occurred when getting prediction from model: ");

    private final String message;

    TextAnalysisExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
