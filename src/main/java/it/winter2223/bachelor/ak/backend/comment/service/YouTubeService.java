package it.winter2223.bachelor.ak.backend.comment.service;

import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;

import java.util.List;

public interface YouTubeService {

    VideoListResponse fetchMostPopularYTVideos();

    List<Comment> fetchYTCommentsByVideoIds(VideoListResponse ytVideos);
}
