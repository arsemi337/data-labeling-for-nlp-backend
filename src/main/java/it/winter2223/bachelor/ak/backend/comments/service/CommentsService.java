package it.winter2223.bachelor.ak.backend.comments.service;

import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;

public interface CommentsService {

    CommentOutput testSaveMethod(CommentInput commentInput);

    CommentOutput testReadMethod(String name);
}
