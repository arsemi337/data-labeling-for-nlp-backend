package it.winter2223.bachelor.ak.backend.comments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Conference")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class CommentsController {
    private final CommentsService commentsService;

    @GetMapping("/comments")
    @Operation(summary = "Test comments")
    ResponseEntity<String> testController() {
        return ResponseEntity.ok().body(commentsService.testMethod());
    }
}
