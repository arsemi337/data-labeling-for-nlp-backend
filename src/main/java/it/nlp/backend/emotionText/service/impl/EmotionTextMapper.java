package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.model.EmotionText;

class EmotionTextMapper {

    EmotionTextOutput mapToEmotionTextOutput(EmotionText emotionText) {
        return EmotionTextOutput.builder()
                .emotionTextId(emotionText.getEmotionTextId())
                .content(emotionText.getContent())
                .build();
    }
}
