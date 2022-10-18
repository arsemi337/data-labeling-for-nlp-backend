package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;

import java.util.UUID;

class CommentMapper {

    CommentOutput mapToCommentOutput(Comment comment) {
        return CommentOutput.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .build();
    }

    Comment mapToComment(CommentInput commentInput, boolean isAssigned) {
        return Comment.builder()
                .commentId(UUID.randomUUID())
                .content(commentInput.content())
                .isAssigned(isAssigned)
                .build();
    }
}
