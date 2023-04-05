package it.winter2223.bachelor.ak.backend.emotionText.service;

import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;

import java.util.List;

public interface InternetCommentService {

    List<EmotionText> fetchYTComments();
}
