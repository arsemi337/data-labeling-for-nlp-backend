package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.model.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.InternetCommentService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("NLPEnabled")
public class CommentScheduler {

    private final CommentRepository commentRepository;
    private final InternetCommentService internetCommentService;

    CommentScheduler(CommentRepository commentRepository, InternetCommentService internetCommentService) {
        this.commentRepository = commentRepository;
        this.internetCommentService = internetCommentService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void downloadYTComments() {
        List<Comment> comments;

        comments = internetCommentService.fetchInternetComments();

        commentRepository.saveAll(comments);
    }
}
