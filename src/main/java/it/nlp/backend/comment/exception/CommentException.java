package it.nlp.backend.comment.exception;

import it.nlp.backend.exception.BusinessLogicException;

public class CommentException extends BusinessLogicException {

    public CommentException(String errorMessage) {
        super(errorMessage);
    }

    public CommentException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
