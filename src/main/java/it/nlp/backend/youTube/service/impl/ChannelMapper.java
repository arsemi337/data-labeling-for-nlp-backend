package it.nlp.backend.youTube.service.impl;

import it.nlp.backend.utils.TimeSupplier;
import it.nlp.backend.youTube.dto.ChannelOutput;
import it.nlp.backend.youTube.model.Channel;

public class ChannelMapper {

    private final TimeSupplier timeSupplier;

    public ChannelMapper(TimeSupplier timeSupplier) {
        this.timeSupplier = timeSupplier;
    }

    public ChannelOutput mapToChannelOutput(Channel channel) {
        return ChannelOutput.builder()
                .id(channel.getId())
                .createdAt(channel.getCreatedAt())
                .title(channel.getTitle())
                .description(channel.getDescription())
                .customUrl(channel.getCustomUrl())
                .subscriberCount(channel.getSubscriberCount())
                .viewCount(channel.getViewCount())
                .videoCount(channel.getVideoCount())
                .uploadPlaylistId(channel.getUploadPlaylistId())
                .build();
    }

    public Channel mapToChannel(com.google.api.services.youtube.model.Channel channel) {
        return Channel.builder()
                .id(channel.getId())
                .createdAt(timeSupplier.get())
                .title(channel.getSnippet().getTitle())
                .description(channel.getSnippet().getDescription())
                .customUrl(channel.getSnippet().getCustomUrl())
                .subscriberCount(channel.getStatistics().getSubscriberCount())
                .viewCount(channel.getStatistics().getViewCount())
                .videoCount(channel.getStatistics().getVideoCount())
                .uploadPlaylistId(channel.getContentDetails().getRelatedPlaylists().getUploads())
                .build();
    }
}
