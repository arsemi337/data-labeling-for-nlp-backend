package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentEmotionAssignmentOutput(UUID assignmentId, UUID commentId, EmotionDto emotionDto) {
}
