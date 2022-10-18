package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = new CommentMapper();
    }

    @Override
    public String putComments(List<CommentInput> commentInputList, boolean isAssigned) {
        commentInputList.stream()
                .map(commentInput -> commentMapper.mapToComment(commentInput, isAssigned))
                .forEach(commentRepository::save);
        return "Done";
    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
