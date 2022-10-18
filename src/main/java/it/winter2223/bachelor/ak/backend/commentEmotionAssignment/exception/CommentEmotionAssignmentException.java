package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class CommentEmotionAssignmentException extends BusinessLogicException {

    public CommentEmotionAssignmentException(String errorMessage) {
        super(errorMessage);
    }
}
