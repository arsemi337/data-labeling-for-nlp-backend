package it.winter2223.bachelor.ak.backend.comment.service;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    List<CommentOutput> fetchYTComments();

    Page<CommentOutput> fetchCommentsList(Pageable pageable);

    List<CommentOutput> fetchCommentsToBeAssigned(String userId, String commentsNumber);
}
