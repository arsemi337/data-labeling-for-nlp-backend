package it.winter2223.bachelor.ak.backend.emotionAnalysis.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class EmotionAnalysisException extends BusinessLogicException {

    public EmotionAnalysisException(String errorMessage) {
        super(errorMessage);
    }

    public EmotionAnalysisException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
