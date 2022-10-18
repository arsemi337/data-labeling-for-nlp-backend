package it.winter2223.bachelor.ak.backend.comments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@Tag(name = "Conference")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class CommentsController {
    private final CommentsService commentsService;

    @PostMapping("/{isAssigned}")
    @Operation(summary = "Produce comments")
    ResponseEntity<String> putComments(
            @RequestBody
            @NotEmpty(message = "Input comments list cannot be empty. ")
            List<@Valid CommentInput> commentInputList,
            @PathVariable boolean isAssigned) {
        return ResponseEntity.ok(commentsService.putComments(commentInputList, isAssigned));
    }

    @GetMapping("/comments")
    @Operation(summary = "Fetch list of comments")
    ResponseEntity<Page<CommentOutput>> fetchCommentsList(Pageable pageable) {
        return ResponseEntity.ok(commentsService.fetchCommentsList(pageable));
    }
}
