package it.nlp.backend.emotionText.service;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

import java.util.List;

public interface YouTubeService {

    VideoListResponse fetchMostPopularYTVideos();
    CommentThreadListResponse fetchMostPopularYTComments(String videoId);
    ChannelListResponse getChannelInformation(List<String> channelId);
}
