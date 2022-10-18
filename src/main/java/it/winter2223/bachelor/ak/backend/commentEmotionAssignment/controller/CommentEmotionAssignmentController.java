package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.CommentEmotionAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Comment emotion assignment")
@RequestMapping("/api/v1/assignment")
@RequiredArgsConstructor
class CommentEmotionAssignmentController {

    private final CommentEmotionAssignmentService commentEmotionAssignmentService;

    @PostMapping
    @Operation(summary = "Post comment-emotion assignment")
    ResponseEntity<CommentEmotionAssignmentOutput> postCommentEmotionAssignment(
            @RequestBody CommentEmotionAssignmentInput commentEmotionAssignmentInput) {
        return ResponseEntity.ok(commentEmotionAssignmentService.postCommentEmotionAssignment(commentEmotionAssignmentInput));
    }
}