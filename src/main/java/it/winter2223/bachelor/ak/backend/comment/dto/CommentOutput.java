package it.winter2223.bachelor.ak.backend.comment.dto;

import lombok.Builder;

@Builder
public record CommentOutput(String commentId, String content) {
}
