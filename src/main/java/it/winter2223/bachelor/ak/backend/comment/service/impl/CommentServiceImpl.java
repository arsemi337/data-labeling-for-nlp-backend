package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentInput;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.config.YouTubeServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
class CommentServiceImpl implements CommentService {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    private final YouTube youTubeService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    CommentServiceImpl(YouTubeServiceConfig youTubeServiceConfig, CommentRepository commentRepository)
            throws GeneralSecurityException, IOException {
        this.youTubeService = youTubeServiceConfig.getService();
        this.commentRepository = commentRepository;
        this.commentMapper = new CommentMapper();
    }

    @Override
    public String getYTComments() {
        VideoListResponse response = null;
        try {
            System.out.println(youtubeApiKey);
            YouTube.Videos.List request = youTubeService.videos()
                    .list(List.of("snippet"));
             response = request.setKey(youtubeApiKey)
                    .setChart("mostPopular")
                    .setRegionCode("pl")
                    .setFields("items(id, snippet(title))")
                    .execute();
            System.out.println(response);
        } catch (IOException e) {

        }
        return response.getItems().toString();
    }

    @Override
    public String putComments(List<CommentInput> commentInputList, boolean isAssigned) {
        commentInputList.stream()
                .map(commentInput -> commentMapper.mapToComment(commentInput, isAssigned))
                .forEach(commentRepository::save);
        return "Done";
    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
