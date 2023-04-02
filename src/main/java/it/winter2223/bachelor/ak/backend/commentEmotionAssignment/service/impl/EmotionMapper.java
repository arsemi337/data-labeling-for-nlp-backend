package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.impl;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.EmotionDto;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.model.Emotion;

class EmotionMapper {

    EmotionDto mapToEmotionDto(Emotion emotion) {
        return EmotionDto.valueOf(emotion.toString());
    }
}
