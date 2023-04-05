package it.winter2223.bachelor.ak.backend.emotionText.service;

import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

public interface YouTubeService {

    VideoListResponse fetchMostPopularYTVideos();
    CommentThreadListResponse fetchMostPopularYTComments(String videoId);
}
