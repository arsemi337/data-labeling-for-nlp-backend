package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto;

import java.util.UUID;

public record CommentEmotionAssignmentInput(UUID commentId, String emotion) {
}
