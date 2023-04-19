package it.nlp.backend.youTube.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Channel {

    @Id
    private String id;
    private LocalDateTime createdAt;
    private String title;
    private String description;
    private String customUrl;
    private BigInteger subscriberCount;
    private BigInteger viewCount;
    private BigInteger videoCount;
    private String uploadPlaylistId;
}
