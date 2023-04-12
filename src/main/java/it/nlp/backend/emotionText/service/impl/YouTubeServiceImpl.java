package it.nlp.backend.emotionText.service.impl;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import it.nlp.backend.emotionText.service.YouTubeService;
import it.nlp.backend.config.YouTubeServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Component
public class YouTubeServiceImpl implements YouTubeService {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    private final YouTube youTube;
    private final Logger logger;

    YouTubeServiceImpl(YouTubeServiceConfig youTubeServiceConfig)
            throws GeneralSecurityException, IOException {
        this.youTube = youTubeServiceConfig.getService();
        this.logger = LoggerFactory.getLogger(YouTubeServiceImpl.class);
    }

    @Override
    public VideoListResponse fetchMostPopularYTVideos() {
        VideoListResponse videos;
        try {
            YouTube.Videos.List request = youTube.videos()
                    .list(List.of("id"));
            videos = request.setKey(youtubeApiKey)
                    .setChart("mostPopular")
                    .setRegionCode("pl")
                    .setMaxResults(50L)
                    .setFields("items(id)")
                    .execute();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage());
        }
        return videos;
    }

    @Override
    public CommentThreadListResponse fetchMostPopularYTComments(String videoId) {
        CommentThreadListResponse commentsResponse;
        try {
            YouTube.CommentThreads.List commentsRequest = youTube.commentThreads().list(List.of("snippet"));
            commentsResponse = commentsRequest.setKey(youtubeApiKey)
                    .setPart(List.of("snippet"))
                    .setVideoId(videoId)
                    .setMaxResults(10L)
                    .setOrder("relevance")
                    .setFields("items(snippet(topLevelComment(id))), items(snippet(topLevelComment(snippet(textDisplay))))")
                    .execute();

        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
            commentsResponse = null;
        }
        return commentsResponse;
    }
}
