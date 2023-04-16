package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.emotionText.service.InternetEmotionTextService;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("NLPEnabled")
public class EmotionTextScheduler {

    private final EmotionTextRepository emotionTextRepository;
    private final InternetEmotionTextService internetEmotionTextService;

    EmotionTextScheduler(EmotionTextRepository emotionTextRepository,
                         InternetEmotionTextService internetEmotionTextService) {
        this.emotionTextRepository = emotionTextRepository;
        this.internetEmotionTextService = internetEmotionTextService;
    }

    //TODO: add new method to scheduler
    @Scheduled(cron = "${cron.expression}")
    public void downloadYTComments() {
        List<EmotionText> emotionTexts;

        emotionTexts = internetEmotionTextService.fetchYTCommentsFromPopularVideos();

        emotionTextRepository.saveAll(emotionTexts);
    }
}
