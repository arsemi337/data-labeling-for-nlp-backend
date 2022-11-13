package it.winter2223.bachelor.ak.backend.comment.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {
    @Id
    private String commentId;

    private String content;

    private boolean isAssigned;
}
