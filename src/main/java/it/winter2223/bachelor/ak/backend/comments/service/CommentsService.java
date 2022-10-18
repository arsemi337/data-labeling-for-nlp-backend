package it.winter2223.bachelor.ak.backend.comments.service;

import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentsService {

    String putComments(List<CommentInput> commentInputList, boolean isAssigned);

    Page<CommentOutput> fetchCommentsList(Pageable number);
}
