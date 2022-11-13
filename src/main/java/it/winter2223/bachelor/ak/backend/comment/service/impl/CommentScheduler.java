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
        System.out.println("starting");
        List<Comment> comments;

        VideoListResponse ytVideos = youTubeService.fetchMostPopularYTVideos();
        if (ytVideos == null) {
            return;
        }
        System.out.println("videos: " + ytVideos.getItems().size());

        comments = youTubeService.fetchYTCommentsByVideoIds(ytVideos);

        System.out.println("comments: " + comments.size());

        commentRepository.saveAll(comments);

        System.out.println("finished");
    }
}
