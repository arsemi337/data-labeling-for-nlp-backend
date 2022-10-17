package it.winter2223.bachelor.ak.backend.comments.service.impl;

import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comments.exception.CommentsException;
import it.winter2223.bachelor.ak.backend.comments.persistence.Model;
import it.winter2223.bachelor.ak.backend.comments.repository.CommentsRepository;
import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import org.springframework.stereotype.Service;

import static it.winter2223.bachelor.ak.backend.comments.exception.CommentsExceptionMessages.INVALID_WHATEVER;

@Service
class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;

    CommentsServiceImpl(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Override
    public CommentOutput testSaveMethod(CommentInput commentInput) {
        commentsRepository.save(new Model(commentInput.name()));
        return new CommentOutput(commentInput.name());
    }

    @Override
    public CommentOutput testReadMethod(String name) {
        Model model = commentsRepository.findByName(name).orElseThrow(() -> new CommentsException(INVALID_WHATEVER.getMessage()));
        return new CommentOutput(model.name());
    }
}
