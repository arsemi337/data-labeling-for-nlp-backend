package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.emotionText.service.InternetCommentService;
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
    private final InternetCommentService internetCommentService;

    EmotionTextScheduler(EmotionTextRepository emotionTextRepository,
                         InternetCommentService internetCommentService) {
        this.emotionTextRepository = emotionTextRepository;
        this.internetCommentService = internetCommentService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void downloadYTComments() {
        List<EmotionText> emotionTexts;

        emotionTexts = internetCommentService.fetchYTCommentsOfPopularVideos();

        emotionTextRepository.saveAll(emotionTexts);
    }
}
