package it.nlp.backend.emotionAnalysis.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record CommentEmotionOutput(EmotionDto mostProbableEmotion, Map<EmotionDto, Float> emotionToProbabilityMap) {
}
