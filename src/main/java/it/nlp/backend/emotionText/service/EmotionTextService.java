package it.nlp.backend.emotionText.service;

import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmotionTextService {

    List<EmotionTextOutput> fetchYTCommentsFromPopularVideos();
    List<EmotionTextOutput> fetchYTCommentsFromVideosOfSavedChannels();
    Page<EmotionTextOutput> fetchEmotionTexts(Pageable pageable);
    List<EmotionTextOutput> fetchEmotionTextsToBeAssigned(String userEmail, String textsNumber);
}
