package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import it.winter2223.bachelor.ak.backend.config.YouTubeServiceConfig;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.github.pemistahl.lingua.api.Language.POLISH;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.COMMENTS_FETCHING_ERROR;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.VIDEOS_FETCHING_ERROR;

@Component
public class YouTubeServiceImpl implements YouTubeService {

    private final CommentRepository commentRepository;
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    private final YouTube youTube;
    private final LanguageDetector detector;

    YouTubeServiceImpl(YouTubeServiceConfig youTubeServiceConfig, CommentRepository commentRepository)
            throws GeneralSecurityException, IOException {
        this.youTube = youTubeServiceConfig.getService();
        this.commentRepository = commentRepository;
        this.detector = LanguageDetectorBuilder.fromAllLanguages().build();
    }

    @Override
    public VideoListResponse fetchMostPopularYTVideos() {
        VideoListResponse videos;
        try {
            YouTube.Videos.List request = youTube.videos()
                    .list(List.of("id"));
            videos = request.setKey(youtubeApiKey)
                    .setChart("mostPopular")
                    .setRegionCode("pl")
                    .setMaxResults(50L)
                    .setFields("items(id)")
                    .execute();
        } catch (IOException ioException) {
            throw new CommentException(VIDEOS_FETCHING_ERROR.getMessage(), ioException);
        }
        return videos;
    }

    @Override
    public List<Comment> fetchYTCommentsByVideoIds(VideoListResponse ytVideos) {
        List<Comment> comments = new ArrayList<>();
        CommentThreadListResponse commentsResponse;
        for (Video video : ytVideos.getItems()) {
            commentsResponse = fetchMostPopularYTComments(video.getId());
            if (commentsResponse == null) {
                continue;
            }

            commentsResponse.getItems().forEach(commentThread -> {
                com.google.api.services.youtube.model.Comment ytComment = commentThread.getSnippet().getTopLevelComment();

                String commentId = ytComment.getId();
                String commentContent = ytComment.getSnippet().getTextDisplay();

                addYTCommentToComments(comments, commentId, commentContent);
            });
        }
        return comments;
    }

    private CommentThreadListResponse fetchMostPopularYTComments(String videoId) {
        CommentThreadListResponse commentsResponse;
        try {
            YouTube.CommentThreads.List commentsRequest = youTube.commentThreads().list(List.of("snippet"));
            commentsResponse = commentsRequest.setKey(youtubeApiKey)
                    .setPart(List.of("snippet"))
                    .setVideoId(videoId)
                    .setMaxResults(50L)
                    .setOrder("relevance")
                    .setFields("items(snippet(topLevelComment(id))), items(snippet(topLevelComment(snippet(textDisplay))))")
                    .execute();

        } catch (IOException ioException) {
            throw new CommentException(COMMENTS_FETCHING_ERROR.getMessage(), ioException);
        }
        return commentsResponse;
    }

    private void addYTCommentToComments(List<Comment> comments, String commentId, String commentContent) {
        commentContent = removeHtmlTags(commentContent);
        if (commentRepository.findById(commentId).isEmpty() && isCommentLengthValid(commentContent) && isCommentPolish(commentContent)) {
            comments.add(Comment.builder()
                    .commentId(commentId)
                    .content(commentContent)
                    .isAssigned(false)
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
