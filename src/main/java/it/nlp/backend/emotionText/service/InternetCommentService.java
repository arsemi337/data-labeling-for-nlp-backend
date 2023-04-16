package it.nlp.backend.emotionText.service;

import it.nlp.backend.emotionText.model.EmotionText;

import java.util.List;

public interface InternetCommentService {

    List<EmotionText> fetchYTCommentsOfPopularVideos();
}
