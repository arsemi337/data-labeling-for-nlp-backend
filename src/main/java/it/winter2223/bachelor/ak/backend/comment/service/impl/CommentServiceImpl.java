package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.comment.service.InternetCommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final InternetCommentService internetCommentService;
    private final CommentMapper commentMapper;

    CommentServiceImpl(CommentRepository commentRepository, InternetCommentService internetCommentService) {
        this.commentRepository = commentRepository;
        this.internetCommentService = internetCommentService;
        this.commentMapper = new CommentMapper();
    }

    @Override
    public List<CommentOutput> fetchYTComments() {
        List<Comment> comments;
        List<CommentOutput> commentOutputList = new ArrayList<>();

        comments = internetCommentService.fetchYTCommentsOfPopularVideos();

        comments.forEach(c -> commentOutputList.add(commentMapper.mapToCommentOutput(commentRepository.save(c))));
        return commentOutputList;

    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
