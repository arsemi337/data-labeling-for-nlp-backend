package it.nlp.backend.exception.messages;

public enum EmotionAnalysisExceptionMessages {
    FAILED_TO_LOAD_NLP_MODEL("Failed to load model responsible for inferring emotions from texts"),
    FAILED_TO_INFER_EMOTION("Failed to infer an emotion from a given text");

    private final String message;

    EmotionAnalysisExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
