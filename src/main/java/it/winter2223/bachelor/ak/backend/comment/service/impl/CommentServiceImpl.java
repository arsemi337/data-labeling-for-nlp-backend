package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.comment.service.InternetCommentService;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.persistence.CommentEmotionAssignment;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.repository.CommentEmotionAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentEmotionAssignmentRepository assignmentRepository;
    private final InternetCommentService internetCommentService;
    private final CommentMapper commentMapper;

    CommentServiceImpl(CommentRepository commentRepository,
                       CommentEmotionAssignmentRepository assignmentRepository,
                       InternetCommentService internetCommentService) {
        this.commentRepository = commentRepository;
        this.assignmentRepository = assignmentRepository;
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
        return commentRepository.findAll(pageable)
                .map(commentMapper::mapToCommentOutput);
    }

    @Override
    public List<CommentOutput> fetchCommentsToBeAssigned(String userId) {
        List<CommentEmotionAssignment> assignments = assignmentRepository.findByUserId(userId);
        List<String> userAssignedCommentsIds = new ArrayList<>();
        assignments.forEach(assignment -> userAssignedCommentsIds.add(assignment.getCommentId()));

        List<Comment> notAssignedComments = new ArrayList<>(commentRepository.findAll().stream()
                .filter(comment -> !userAssignedCommentsIds.contains(comment.getCommentId())).toList());
        Collections.shuffle(notAssignedComments);
        int elementsNumber = Math.min(notAssignedComments.size(), 20);
        List<Comment> commentsSublist = notAssignedComments.subList(0, elementsNumber - 1);
        List<CommentOutput> commentsToBeAssigned = new ArrayList<>();
        commentsSublist.forEach(comment -> commentsToBeAssigned.add(commentMapper.mapToCommentOutput(comment)));
        return commentsToBeAssigned;
    }
}
