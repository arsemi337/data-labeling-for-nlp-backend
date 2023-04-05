package it.winter2223.bachelor.ak.backend.emotionText.service;

import it.winter2223.bachelor.ak.backend.emotionText.dto.EmotionTextOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmotionTextService {

    List<EmotionTextOutput> fetchYTComments();

    Page<EmotionTextOutput> fetchEmotionTexts(Pageable pageable);

    List<EmotionTextOutput> fetchEmotionTextsToBeAssigned(String userId, String commentsNumber);
}
