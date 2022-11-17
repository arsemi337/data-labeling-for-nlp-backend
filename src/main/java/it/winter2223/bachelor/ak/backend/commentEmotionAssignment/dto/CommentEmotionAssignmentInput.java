package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto;

import lombok.Builder;

@Builder
public record CommentEmotionAssignmentInput(String userId, String commentId, String emotion) {
}
