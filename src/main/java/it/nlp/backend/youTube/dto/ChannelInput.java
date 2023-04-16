package it.nlp.backend.youTube.dto;

import lombok.Builder;

@Builder
public record ChannelInput(
        String channelId
) {
}
