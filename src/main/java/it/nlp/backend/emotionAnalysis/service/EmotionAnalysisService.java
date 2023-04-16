package it.nlp.backend.emotionAnalysis.service;

import it.nlp.backend.emotionAnalysis.dto.CommentEmotionInput;
import it.nlp.backend.emotionAnalysis.dto.CommentEmotionOutput;

public interface EmotionAnalysisService {
    CommentEmotionOutput classifyCommentEmotion(CommentEmotionInput commentInput);
}
