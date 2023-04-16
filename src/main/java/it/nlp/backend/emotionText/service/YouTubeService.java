package it.nlp.backend.emotionText.service;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import java.util.List;

public interface YouTubeService {

    List<String> fetchIdsOfMostPopularVideos();
    CommentThreadListResponse fetchMostPopularComments(String videoId);
    ChannelListResponse fetchChannelInformation(List<String> channelId);
    List<String> fetchIdsOfNewestChannelVideos(String channelUploadPlaylistId);
}
