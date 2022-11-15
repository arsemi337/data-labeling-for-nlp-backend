package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.InternetCommentService;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.github.pemistahl.lingua.api.Language.POLISH;

@Component
public class YouTubeCommentServiceImpl implements InternetCommentService {

    private final CommentRepository commentRepository;
    private final YouTubeService youTubeService;
    private final LanguageDetector detector;

    YouTubeCommentServiceImpl(CommentRepository commentRepository, YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
        this.commentRepository = commentRepository;
        this.detector = LanguageDetectorBuilder.fromAllLanguages().build();
    }

    @Override
    public List<Comment> fetchYTCommentsOfPopularVideos() {
        List<Comment> comments = new ArrayList<>();

        VideoListResponse ytVideos = youTubeService.fetchMostPopularYTVideos();
        if (ytVideos == null) {
            return comments;
        }

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

                addYTCommentToComments(comments, commentId, commentContent);
            });
        }
        return comments;
    }

    private void addYTCommentToComments(List<Comment> comments, String commentId, String commentContent) {
        commentContent = removeHtmlTags(commentContent);
        if (commentRepository.findById(commentId).isEmpty() && isCommentLengthValid(commentContent) && isCommentPolish(commentContent)) {
            comments.add(Comment.builder()
                    .commentId(commentId)
                    .content(commentContent)
                    .assignmentsNumber(0)
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
