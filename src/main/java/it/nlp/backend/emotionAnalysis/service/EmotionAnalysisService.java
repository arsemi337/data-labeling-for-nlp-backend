package it.nlp.backend.emotionAnalysis.service;

import it.nlp.backend.emotionAnalysis.dto.TextEmotionInput;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionOutput;

public interface EmotionAnalysisService {
    TextEmotionOutput classifyTextEmotion(TextEmotionInput textEmotionInput);
}
