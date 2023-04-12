package it.nlp.backend.emotionAnalysis.exception;

import it.nlp.backend.exception.BusinessLogicException;

public class EmotionAnalysisException extends BusinessLogicException {

    public EmotionAnalysisException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
