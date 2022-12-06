package it.winter2223.bachelor.ak.backend.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static it.winter2223.bachelor.ak.backend.comment.controller.swagger.CommentSwaggerSample.PAGEABLE_EXAMPLE;

@RestController
@Tag(name = "Comment")
@RequestMapping("/api/v1/comment")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
class CommentController {
    private final CommentService commentService;

    @GetMapping("/youtube")
    @Operation(summary = "Fetch comments from You Tube")
    @PreAuthorize("hasAuthority('USER_READ_WRITE')")
    ResponseEntity<List<CommentOutput>> getYTComments() {
        return ResponseEntity.ok(commentService.fetchYTComments());
    }

    @GetMapping("/all")
    @Operation(summary = "Fetch list of comments from database")
    @PreAuthorize("hasAuthority('USER_READ_WRITE')")
    ResponseEntity<Page<CommentOutput>> fetchCommentsList(
            @Parameter(example = PAGEABLE_EXAMPLE)
            Pageable pageable) {
        return ResponseEntity.ok(commentService.fetchCommentsList(pageable));
    }

    @GetMapping
    @Operation(summary = "Fetch comments to be assigned by the user with userId")
    @PreAuthorize("hasAuthority('USER_READ_WRITE')")
    ResponseEntity<List<CommentOutput>> fetchCommentsToBeAssigned(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "commentsNumber") String commentsNumber) {
        return ResponseEntity.ok(commentService.fetchCommentsToBeAssigned(userId, commentsNumber));
    }
}
