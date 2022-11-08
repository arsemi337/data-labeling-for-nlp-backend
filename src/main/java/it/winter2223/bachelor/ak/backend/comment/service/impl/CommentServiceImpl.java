package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import it.winter2223.bachelor.ak.backend.config.YouTubeServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
    public List<CommentOutput> getYTComments() {
        VideoListResponse response = null;
        try {
            YouTube.Videos.List request = youTubeService.videos()
                    .list(List.of("id"));
             response = request.setKey(youtubeApiKey)
                    .setChart("mostPopular")
                    .setRegionCode("pl")
                    .setFields("items(id)")
                    .execute();
        } catch (IOException e) {

        }

        List<Comment> comments = new ArrayList<>();
        try {
            CommentThreadListResponse commentsResponse = new CommentThreadListResponse();

            System.out.println(commentsResponse.getEtag());

            YouTube.CommentThreads.List commentsRequest = youTubeService.commentThreads().list(List.of("snippet"));
            commentsResponse = commentsRequest.setKey(youtubeApiKey)
                    .setPart(List.of("snippet"))
                    .setVideoId(response.getItems().get(0).getId())
                    .setFields("items(snippet(topLevelComment(id))), items(snippet(topLevelComment(snippet(textDisplay))))")
                    .execute();

            commentsResponse.getItems().forEach(commentThread -> {
                System.out.println(commentThread.getSnippet().getTopLevelComment().toString());

                com.google.api.services.youtube.model.Comment ytComment = commentThread.getSnippet().getTopLevelComment();

                if (commentRepository.findById(ytComment.getId()).isEmpty()) {
                    comments.add(Comment.builder()
                            .commentId(ytComment.getId())
                            .content(ytComment.getSnippet().getTextDisplay())
                            .isAssigned(false)
                            .build());
                }

            });
        } catch (IOException ioException) {

        }

        List<CommentOutput> commentOutputList = new ArrayList<>();
        comments.forEach(c -> commentOutputList.add(commentMapper.mapToCommentOutput(commentRepository.save(c))));
        return commentOutputList;

    }

//    @Override
//    public String putComments(List<CommentInput> commentInputList, boolean isAssigned) {
//        commentInputList.stream()
//                .map(commentInput -> commentMapper.mapToComment(commentInput, isAssigned))
//                .forEach(commentRepository::save);
//        return "Done";
//    }

    @Override
    public Page<CommentOutput> fetchCommentsList(Pageable pageable) {
        return commentRepository.findByIsAssigned(false, pageable)
                .map(commentMapper::mapToCommentOutput);
    }
}
