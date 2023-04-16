package it.nlp.backend.emotionText.service.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.emotionText.service.InternetEmotionTextService;
import it.nlp.backend.emotionText.service.YouTubeService;
import it.nlp.backend.utils.TimeSupplier;
import it.nlp.backend.youTube.model.Channel;
import it.nlp.backend.youTube.repository.ChannelRepository;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.github.pemistahl.lingua.api.Language.POLISH;

@Component
public class InternetEmotionTextServiceImpl implements InternetEmotionTextService {

    private final EmotionTextRepository textRepository;
    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;
    private final LanguageDetector detector;
    private final TimeSupplier timeSupplier;

    InternetEmotionTextServiceImpl(EmotionTextRepository textRepository,
                                   ChannelRepository channelRepository, YouTubeService youTubeService,
                                   TimeSupplier timeSupplier) {
        this.textRepository = textRepository;
        this.channelRepository = channelRepository;
        this.youTubeService = youTubeService;
        this.timeSupplier = timeSupplier;
        this.detector = LanguageDetectorBuilder.fromAllLanguages().build();
    }

    @Override
    public List<EmotionText> fetchYTCommentsFromPopularVideos() {
        List<EmotionText> emotionTextList = new ArrayList<>();

        List<String> videoIdList = youTubeService.fetchIdsOfMostPopularVideos();

        List<Comment> mostPopularComments = getMostPopularCommentsFromVideos(videoIdList);

        addValidCommentsToEmotionTextList(emotionTextList, mostPopularComments);

//        videoIdList.stream()
//                .map(youTubeService::fetchMostPopularComments)
//                .filter(Objects::nonNull)
//                .map(CommentThreadListResponse::getItems)
//                .flatMap(List::stream)
//                .map(commentThread -> commentThread.getSnippet().getTopLevelComment())
//                .forEach(comment -> {
//                    String commentId = comment.getId();
//                    String commentContent = comment.getSnippet().getTextDisplay();
//                    addYTCommentToEmotionTexts(emotionTexts, commentId, commentContent);
//                });

//        videoIdList.forEach(videoId -> {
//            CommentThreadListResponse commentsResponse =
//                    youTubeService.fetchMostPopularComments(videoId);
//            if (commentsResponse == null) {
//                return;
//            }
//
//            commentsResponse.getItems().forEach(commentThread -> {
//                Comment ytComment = commentThread.getSnippet().getTopLevelComment();
//
//                String commentId = ytComment.getId();
//                String commentContent = ytComment.getSnippet().getTextDisplay();
//
//                addYTCommentToEmotionTexts(emotionTexts, commentId, commentContent);
//            });
//        });

        return emotionTextList;
    }

    @Override
    public List<EmotionText> fetchYTCommentsFromVideosOfSavedChannels() {
        List<EmotionText> emotionTextList = new ArrayList<>();

        List<String> videoIdList = channelRepository.findAll().stream()
                .map(Channel::getUploadPlaylistId)
                .map(youTubeService::fetchIdsOfNewestChannelVideos)
                .flatMap(List::stream)
                .toList();

        List<Comment> mostPopularComments = getMostPopularCommentsFromVideos(videoIdList);

        addValidCommentsToEmotionTextList(emotionTextList, mostPopularComments);

        return emotionTextList;
    }

    private List<Comment> getMostPopularCommentsFromVideos(List<String> videoIdList) {
        return videoIdList.stream()
                .map(youTubeService::fetchMostPopularComments)
                .filter(Objects::nonNull)
                .map(CommentThreadListResponse::getItems)
                .flatMap(List::stream)
                .map(commentThread -> commentThread.getSnippet().getTopLevelComment())
                .toList();
    }

    private void addValidCommentsToEmotionTextList(List<EmotionText> emotionTexts, List<Comment> mostPopularComments) {
        mostPopularComments.forEach(comment -> {
            String commentId = comment.getId();
            String commentContent = comment.getSnippet().getTextDisplay();
            addYTCommentToEmotionTexts(emotionTexts, commentId, commentContent);
        });
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
