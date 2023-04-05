package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;
import it.winter2223.bachelor.ak.backend.emotionText.repository.EmotionTextRepository;
import it.winter2223.bachelor.ak.backend.emotionText.service.InternetCommentService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("NLPEnabled")
public class EmotionTextScheduler {

    private final EmotionTextRepository emotionTextRepository;
    private final InternetCommentService internetCommentService;

    EmotionTextScheduler(EmotionTextRepository emotionTextRepository,
                         InternetCommentService internetCommentService) {
        this.emotionTextRepository = emotionTextRepository;
        this.internetCommentService = internetCommentService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void downloadYTComments() {
        List<EmotionText> emotionTexts;

        emotionTexts = internetCommentService.fetchYTComments();

        emotionTextRepository.saveAll(emotionTexts);
    }
}
