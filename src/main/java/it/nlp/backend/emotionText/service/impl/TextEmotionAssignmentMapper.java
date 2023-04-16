package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.nlp.backend.emotionText.model.Emotion;

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
