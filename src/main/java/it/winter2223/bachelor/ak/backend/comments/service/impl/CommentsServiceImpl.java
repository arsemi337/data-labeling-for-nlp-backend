package it.winter2223.bachelor.ak.backend.comments.service.impl;

import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comments.repository.CommentsRepository;
import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final CommentMapper commentMapper;

    CommentsServiceImpl(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
        this.commentMapper = new CommentMapper();
    }

    @Override
    public String putComments(List<CommentInput> commentInputList, boolean isAssigned) {
        commentInputList.stream()
                .map(commentInput -> commentMapper.mapToComment(commentInput, isAssigned))
                .forEach(commentsRepository::save);
        return "Done";
    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentsRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
