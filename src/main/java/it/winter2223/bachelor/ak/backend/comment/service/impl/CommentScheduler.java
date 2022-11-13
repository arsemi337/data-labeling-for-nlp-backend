package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentScheduler {

    private final CommentRepository commentRepository;
    private final YouTubeService youTubeService;

    CommentScheduler(CommentRepository commentRepository, YouTubeService youTubeService) {
        this.commentRepository = commentRepository;
        this.youTubeService = youTubeService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void downloadYTComments() {
        List<Comment> comments;

        VideoListResponse ytVideos = youTubeService.fetchMostPopularYTVideos();
        if (ytVideos == null) {
            return;
        }

        comments = youTubeService.fetchYTCommentsByVideoIds(ytVideos);

        commentRepository.saveAll(comments);
    }
}
