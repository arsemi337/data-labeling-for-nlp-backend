package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.persistence;

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
public class CommentEmotionAssignment {

    @Id
    private UUID commentEmotionAssignmentId;

    private String commentId;

    private Emotion emotion;
}
