package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;
import it.winter2223.bachelor.ak.backend.emotionText.repository.EmotionTextRepository;
import it.winter2223.bachelor.ak.backend.emotionText.service.InternetCommentService;
import it.winter2223.bachelor.ak.backend.emotionText.service.YouTubeService;
import it.winter2223.bachelor.ak.backend.utils.TimeSupplier;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import static com.github.pemistahl.lingua.api.Language.POLISH;

@Component
public class YouTubeCommentServiceImpl implements InternetCommentService {

    private final EmotionTextRepository textRepository;
    private final YouTubeService youTubeService;
    private final LanguageDetector detector;
    private final TimeSupplier timeSupplier;

    YouTubeCommentServiceImpl(EmotionTextRepository textRepository,
                              YouTubeService youTubeService,
                              TimeSupplier timeSupplier) {
        this.textRepository = textRepository;
        this.youTubeService = youTubeService;
        this.timeSupplier = timeSupplier;
        this.detector = LanguageDetectorBuilder.fromAllLanguages().build();
    }

    @Override
    public List<EmotionText> fetchYTComments() {
        List<EmotionText> emotionTexts = new ArrayList<>();

        VideoListResponse ytVideos = youTubeService.fetchMostPopularYTVideos();
        if (ytVideos == null) {
            return emotionTexts;
        }

        // TODO: Take a look at this algorithm for fetching comment threads
        CommentThreadListResponse commentsResponse;
        for (Video video : ytVideos.getItems()) {
            commentsResponse = youTubeService.fetchMostPopularYTComments(video.getId());
            if (commentsResponse == null) {
                continue;
            }

            commentsResponse.getItems().forEach(commentThread -> {
                com.google.api.services.youtube.model.Comment ytComment = commentThread.getSnippet().getTopLevelComment();

                String commentId = ytComment.getId();
                String commentContent = ytComment.getSnippet().getTextDisplay();

                addYTCommentToEmotionTexts(emotionTexts, commentId, commentContent);
            });
        }
        return emotionTexts;
    }

    private void addYTCommentToEmotionTexts(List<EmotionText> emotionTexts, String commentId, String commentContent) {
        commentContent = removeHtmlTags(commentContent);

        if (!textRepository.existsByOriginalSourceId(commentId) &&
                isCommentLengthValid(commentContent) &&
                isCommentPolish(commentContent)) {
            emotionTexts.add(EmotionText.builder()
                    .emotionTextId(UUID.randomUUID())
                    .createdAt(timeSupplier.get())
                    .content(commentContent)
                    .originalSourceId(commentId)
                    .assignedEmotions(new ArrayList<>())
                    .build());
        }
    }

    private String removeHtmlTags(String commentContent) {
        return Jsoup.parse(commentContent).text();
    }

    private boolean isCommentLengthValid(String commentContent) {
        int tokensNumber = new StringTokenizer(commentContent).countTokens();
        return tokensNumber > 5 && tokensNumber < 250;
    }

    private boolean isCommentPolish(String commentContent) {
        return detector.detectLanguageOf(commentContent) == POLISH;
    }
}
