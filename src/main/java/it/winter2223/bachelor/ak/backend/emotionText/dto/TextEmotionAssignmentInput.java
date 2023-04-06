package it.winter2223.bachelor.ak.backend.emotionText.dto;

import lombok.Builder;

@Builder
public record TextEmotionAssignmentInput(String userId, String textId, String emotion) {
}
