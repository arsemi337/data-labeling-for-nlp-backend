package it.winter2223.bachelor.ak.backend.emotionText.service.impl;


import it.winter2223.bachelor.ak.backend.emotionText.dto.EmotionDto;
import it.winter2223.bachelor.ak.backend.emotionText.model.Emotion;

class EmotionMapper {

    EmotionDto mapToEmotionDto(Emotion emotion) {
        return EmotionDto.valueOf(emotion.toString());
    }
}
