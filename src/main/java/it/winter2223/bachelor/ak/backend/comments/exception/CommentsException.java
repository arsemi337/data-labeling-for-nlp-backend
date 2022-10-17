package it.winter2223.bachelor.ak.backend.comments.exception;

import it.winter2223.bachelor.ak.backend.exception.BusinessLogicException;

public class CommentsException extends BusinessLogicException {

    public CommentsException(String errorMessage) {
        super(errorMessage);
    }
}
