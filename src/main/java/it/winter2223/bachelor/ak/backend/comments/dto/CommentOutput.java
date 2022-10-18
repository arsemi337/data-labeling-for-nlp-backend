package it.winter2223.bachelor.ak.backend.comments.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentOutput(UUID commentId, String content) {
}
