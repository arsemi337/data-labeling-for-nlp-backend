package it.winter2223.bachelor.ak.backend.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static it.winter2223.bachelor.ak.backend.comment.controller.swagger.CommentSwaggerSample.PAGEABLE_EXAMPLE;

@RestController
@Tag(name = "Comment")
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
class CommentController {
    private final CommentService commentService;

    @PostMapping("/{isAssigned}")
    @Operation(summary = "Produce comments")
    ResponseEntity<String> postComments(
            @RequestBody
            @NotEmpty(message = "Input comments list cannot be empty. ")
            List<@Valid CommentInput> commentInputList,
            @PathVariable boolean isAssigned) {
        return ResponseEntity.ok(commentService.putComments(commentInputList, isAssigned));
    }

    @GetMapping
    @Operation(summary = "Fetch list of comments")
    ResponseEntity<Page<CommentOutput>> fetchCommentsList(
            @Parameter(example = PAGEABLE_EXAMPLE)
            Pageable pageable) {
        return ResponseEntity.ok(commentService.fetchCommentsList(pageable));
    }
}