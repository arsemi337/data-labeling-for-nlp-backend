package it.winter2223.bachelor.ak.backend.comment.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class CommentException extends BusinessLogicException {

    public CommentException(String errorMessage) {
        super(errorMessage);
    }
}
