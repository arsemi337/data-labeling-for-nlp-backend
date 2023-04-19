package it.nlp.backend.emotionAnalysis.dto;

import lombok.Builder;

@Builder
public record TextEmotionInput(String text) {
}
