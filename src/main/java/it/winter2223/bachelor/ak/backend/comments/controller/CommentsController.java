package it.winter2223.bachelor.ak.backend.comments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comments.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Conference")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class CommentsController {
    private final CommentsService commentsService;

    @PostMapping ("/comments")
    @Operation(summary = "Test save")
    ResponseEntity<CommentOutput> testSaveController(@RequestBody CommentInput commentInput) {
        return ResponseEntity.ok(commentsService.testSaveMethod(commentInput));
    }

    @GetMapping("/comments/{name}")
    @Operation(summary = "Test read")
    ResponseEntity<CommentOutput> testReadController(@PathVariable String name) {
        return ResponseEntity.ok(commentsService.testReadMethod(name));
    }
}
