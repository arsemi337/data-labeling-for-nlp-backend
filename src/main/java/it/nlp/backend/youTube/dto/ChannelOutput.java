package it.nlp.backend.youTube.dto;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record ChannelOutput(
        String id,
        String title,
        String description,
        String customUrl,
        BigInteger subscriberCount,
        BigInteger viewCount,
        BigInteger videoCount
) {
}
