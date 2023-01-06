package it.winter2223.bachelor.ak.backend.emotionAnalysis.dto;

import lombok.Builder;

@Builder
public record CommentEmotionInput(String comment) {
}
