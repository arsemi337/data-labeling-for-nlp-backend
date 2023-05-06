package it.nlp.backend.emotionText.service.impl;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import it.nlp.backend.emotionText.service.YouTubeService;
import it.nlp.backend.config.YouTubeServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
    public List<String> fetchIdsOfMostPopularVideos() {
        List<String> videoIdList = new ArrayList<>();
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

            videos.getItems().forEach(video -> videoIdList.add(video.getId()));
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage());
        }
        return videoIdList;
    }

    @Override
    public CommentThreadListResponse fetchMostPopularComments(String videoId) {
        CommentThreadListResponse commentsResponse;
        try {
            YouTube.CommentThreads.List commentsRequest = youTube.commentThreads().list(List.of("snippet"));
            commentsResponse = commentsRequest.setKey(youtubeApiKey)
                    .setPart(List.of("snippet"))
                    .setVideoId(videoId)
                    .setMaxResults(50L)
                    .setOrder("relevance")
                    .setFields("items(snippet(topLevelComment(id))), items(snippet(topLevelComment(snippet(textDisplay))))")
                    .execute();

        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
            commentsResponse = null;
        }
        return commentsResponse;
    }

    @Override
    public ChannelListResponse fetchChannelInformation(List<String> channelIdList) {
        ChannelListResponse channelListResponse;
        try {
            YouTube.Channels.List request = youTube.channels()
                    .list(List.of("contentDetails","snippet","statistics"));
            channelListResponse = request.setKey(youtubeApiKey)
                    .setId(channelIdList)
                    .setFields("items(id, snippet(title, description, customUrl), contentDetails, statistics)")
                    .execute();
        } catch (IOException ioException) {
            throw new IllegalArgumentException(ioException.getMessage());
        }
        return channelListResponse;
    }

    @Override
    public List<String> fetchIdsOfNewestChannelVideos(String channelUploadPlaylistId) {
        List<String> videoIdList = new ArrayList<>();
        PlaylistItemListResponse playlistItemListResponse;
        try {
            YouTube.PlaylistItems.List request = youTube.playlistItems()
                    .list(List.of("contentDetails"));
            playlistItemListResponse = request.setKey(youtubeApiKey)
                    .setMaxResults(2L)
                    .setPlaylistId(channelUploadPlaylistId)
                    .setFields("items(contentDetails(videoId))")
                    .execute();

            playlistItemListResponse.getItems().forEach(playlistItem ->
                    videoIdList.add(playlistItem.getContentDetails().getVideoId()));
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            return videoIdList;
        }

        return videoIdList;
    }
}
