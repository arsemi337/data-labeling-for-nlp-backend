package it.winter2223.bachelor.ak.backend.emotionText.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TextEmotionAssignmentOutput(UUID userId, UUID textId, EmotionDto emotionDto) {
}
