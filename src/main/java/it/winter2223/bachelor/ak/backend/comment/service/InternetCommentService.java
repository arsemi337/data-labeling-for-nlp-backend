package it.winter2223.bachelor.ak.backend.comment.service;

import it.winter2223.bachelor.ak.backend.comment.model.Comment;

import java.util.List;

public interface InternetCommentService {

    List<Comment> fetchInternetComments();
}
