package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.emotionText.model.Emotion;

import java.util.UUID;

class TextEmotionAssignmentMapper {

    private final EmotionMapper emotionMapper;

    TextEmotionAssignmentMapper() {
        this.emotionMapper = new EmotionMapper();
    }

    TextEmotionAssignmentOutput mapToTextEmotionAssignmentOutput(UUID userId, UUID textId, Emotion emotion) {
        return TextEmotionAssignmentOutput.builder()
                .userId(userId)
                .textId(textId)
                .emotionDto(emotionMapper.mapToEmotionDto(emotion))
                .build();
    }
}
