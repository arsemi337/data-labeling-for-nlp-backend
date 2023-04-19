package it.nlp.backend.youTube.dto;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Builder
public record ChannelOutput(
        String id,
        LocalDateTime createdAt,
        String title,
        String description,
        String customUrl,
        BigInteger subscriberCount,
        BigInteger viewCount,
        BigInteger videoCount,
        String uploadPlaylistId
) {
}
