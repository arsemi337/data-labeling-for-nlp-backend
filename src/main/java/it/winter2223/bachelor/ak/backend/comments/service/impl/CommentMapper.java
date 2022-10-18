package it.winter2223.bachelor.ak.backend.comments.service.impl;

import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comments.persistence.Comment;

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
