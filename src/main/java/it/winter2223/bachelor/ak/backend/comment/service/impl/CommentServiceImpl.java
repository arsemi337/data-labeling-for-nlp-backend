package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final YouTubeService youTubeService;
    private final CommentMapper commentMapper;

    CommentServiceImpl(CommentRepository commentRepository, YouTubeService youTubeService) {
        this.commentRepository = commentRepository;
        this.youTubeService = youTubeService;
        this.commentMapper = new CommentMapper();
    }

    @Override
    public List<CommentOutput> fetchYTComments() {
        List<Comment> comments;
        List<CommentOutput> commentOutputList = new ArrayList<>();

        VideoListResponse ytVideos = youTubeService.fetchMostPopularYTVideos();
        if (ytVideos == null) {
            return commentOutputList;
        }

        comments = youTubeService.fetchYTCommentsByVideoIds(ytVideos);

        comments.forEach(c -> commentOutputList.add(commentMapper.mapToCommentOutput(commentRepository.save(c))));
        return commentOutputList;

    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
