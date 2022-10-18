package it.winter2223.bachelor.ak.backend.comments.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {
    @Id
    private UUID commentId;

    private String content;

    private boolean isAssigned;
}
