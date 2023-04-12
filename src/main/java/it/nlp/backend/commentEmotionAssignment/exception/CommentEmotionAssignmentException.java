package it.nlp.backend.commentEmotionAssignment.exception;

import it.nlp.backend.exception.BusinessLogicException;

public class CommentEmotionAssignmentException extends BusinessLogicException {

    public CommentEmotionAssignmentException(String errorMessage) {
        super(errorMessage);
    }

    public CommentEmotionAssignmentException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
