package it.nlp.backend.emotionAnalysis.dto;

import lombok.Builder;

@Builder
public record CommentEmotionInput(String comment) {
}
