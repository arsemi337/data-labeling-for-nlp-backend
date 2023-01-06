package it.winter2223.bachelor.ak.backend.emotionAnalysis.service;

import it.winter2223.bachelor.ak.backend.emotionAnalysis.dto.CommentEmotionInput;
import it.winter2223.bachelor.ak.backend.emotionAnalysis.dto.CommentEmotionOutput;

public interface EmotionAnalysisService {
    CommentEmotionOutput classifyCommentEmotion(CommentEmotionInput input);
}
