package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.model.EmotionText;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmotionTextMapperTest {

    EmotionTextMapper emotionTextMapper = new EmotionTextMapper();

    @Test
    @DisplayName("when EmotionText is provided, it should be mapped to EmotionTextOutput")
    void shouldMapEmotionTextToEmotionTextOutput() {
        EmotionText emotionText = getEmotionText();

        EmotionTextOutput emotionTextOutput = emotionTextMapper.mapToEmotionTextOutput(emotionText);

        assertEquals(emotionTextOutput.emotionTextId(), emotionText.getEmotionTextId());
        assertEquals(emotionTextOutput.content(), emotionText.getContent());
    }

    private EmotionText getEmotionText() {
        return EmotionText.builder()
                .emotionTextId(UUID.randomUUID())
                .content("testContent")
                .build();
    }
}
