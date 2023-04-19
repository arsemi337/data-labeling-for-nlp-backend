package it.nlp.backend.emotionText.dto;

import lombok.Builder;

@Builder
public record TextEmotionAssignmentsNumberOutput(
        Integer assignmentsCount) {
}
