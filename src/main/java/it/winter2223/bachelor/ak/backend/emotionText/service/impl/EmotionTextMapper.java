package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import it.winter2223.bachelor.ak.backend.emotionText.dto.EmotionTextOutput;
import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;

class EmotionTextMapper {

    EmotionTextOutput mapToEmotionTextOutput(EmotionText emotionText) {
        return EmotionTextOutput.builder()
                .emotionTextId(emotionText.getEmotionTextId())
                .content(emotionText.getContent())
                .build();
    }
}
