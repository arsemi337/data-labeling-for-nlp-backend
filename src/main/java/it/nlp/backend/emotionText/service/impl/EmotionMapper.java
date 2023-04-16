package it.nlp.backend.emotionText.service.impl;


import it.nlp.backend.emotionText.model.Emotion;
import it.nlp.backend.emotionText.dto.EmotionDto;

class EmotionMapper {

    EmotionDto mapToEmotionDto(Emotion emotion) {
        return EmotionDto.valueOf(emotion.toString());
    }
}
