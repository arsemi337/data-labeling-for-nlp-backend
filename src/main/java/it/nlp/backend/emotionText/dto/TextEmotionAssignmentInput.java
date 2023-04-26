package it.nlp.backend.emotionText.dto;

import lombok.Builder;

@Builder
public record TextEmotionAssignmentInput(
        String textId,
        String emotion) {
}
