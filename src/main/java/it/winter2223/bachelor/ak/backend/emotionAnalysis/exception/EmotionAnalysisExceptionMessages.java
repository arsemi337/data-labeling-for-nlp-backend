package it.winter2223.bachelor.ak.backend.emotionAnalysis.exception;

public enum EmotionAnalysisExceptionMessages {
    FAILED_TO_LOAD_NLP_MODEL("Failed to load model responsible for inferring emotions from comments"),
    FAILED_TO_INFER_EMOTION("Failed to infer an emotion from a given comment");

    private final String message;

    EmotionAnalysisExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
