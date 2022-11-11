package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.config.YouTubeServiceConfig;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.github.pemistahl.lingua.api.Language.*;

@Service
class CommentServiceImpl implements CommentService {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    private final YouTube youTubeService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final LanguageDetector detector;

    CommentServiceImpl(YouTubeServiceConfig youTubeServiceConfig, CommentRepository commentRepository)
            throws GeneralSecurityException, IOException {
        this.youTubeService = youTubeServiceConfig.getService();
        this.commentRepository = commentRepository;
        this.commentMapper = new CommentMapper();
        this.detector = LanguageDetectorBuilder.fromAllLanguages().build();
    }

    @Override
    public List<CommentOutput> getYTComments() {
        VideoListResponse videos = null;
        List<Comment> comments = new ArrayList<>();
        try {
            YouTube.Videos.List request = youTubeService.videos()
                    .list(List.of("id"));
            videos = request.setKey(youtubeApiKey)
                    .setChart("mostPopular")
                    .setRegionCode("pl")
                    .setMaxResults(50L)
                    .setFields("items(id)")
                    .execute();
        } catch (IOException e) {

        }

        if (videos != null) {
            for (Video video : videos.getItems()) {
                try {
                    CommentThreadListResponse commentsResponse;

                    YouTube.CommentThreads.List commentsRequest = youTubeService.commentThreads().list(List.of("snippet"));
                    commentsResponse = commentsRequest.setKey(youtubeApiKey)
                            .setPart(List.of("snippet"))
                            .setVideoId(video.getId())
                            .setMaxResults(50L)
                            .setFields("items(snippet(topLevelComment(id))), items(snippet(topLevelComment(snippet(textDisplay))))")
                            .execute();

                    commentsResponse.getItems().forEach(commentThread -> {
                        System.out.println(commentThread.getSnippet().getTopLevelComment().toString());

                        com.google.api.services.youtube.model.Comment ytComment = commentThread.getSnippet().getTopLevelComment();

                        String commentId = ytComment.getId();
                        String commentContent = ytComment.getSnippet().getTextDisplay();

                        commentContent = removeHtmlTags(commentContent);

                        if (commentRepository.findById(commentId).isEmpty() && isCommentLengthValid(commentContent) && isCommentPolish(commentContent)) {
                            comments.add(Comment.builder()
                                    .commentId(commentId)
                                    .content(commentContent)
                                    .isAssigned(false)
                                    .build());
                        }

                    });
                } catch (IOException ioException) {

                }
            }
        }

        List<CommentOutput> commentOutputList = new ArrayList<>();
        comments.forEach(c -> commentOutputList.add(commentMapper.mapToCommentOutput(commentRepository.save(c))));
        return commentOutputList;

    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
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
