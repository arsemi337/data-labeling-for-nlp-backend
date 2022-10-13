package it.winter2223.bachelor.ak.backend.comments.controller;

import it.winter2223.bachelor.ak.backend.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
class CommentsController {
    private final CommentsService commentsService;

    @GetMapping
    ResponseEntity<String> testController() {
        return ResponseEntity.ok().body(commentsService.testMethod());
    }
}
