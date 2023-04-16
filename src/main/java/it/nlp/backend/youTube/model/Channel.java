package it.nlp.backend.youTube.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Channel {

    @Id
    private String id;
    private String title;
    private String description;
    private String customUrl;
    private BigInteger subscriberCount;
    private BigInteger viewCount;
    private BigInteger videoCount;
}
