package it.nlp.backend.emotionText.model;

import it.nlp.backend.comment.exception.CommentException;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static it.nlp.backend.comment.exception.CommentExceptionMessages.CANNOT_COMPARE_NULL_COMMENT;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmotionText implements Comparable<EmotionText> {

    @Id
    private UUID emotionTextId;
    private LocalDateTime createdAt;
    private String content;
    private String originalSourceId;
    private TextSource textSource;
    private List<Emotion> assignedEmotions;

    @Override
    public int compareTo(EmotionText o) {
        if (o == null) {
            throw new CommentException(CANNOT_COMPARE_NULL_COMMENT.getMessage());
        }
        return Integer.compare(assignedEmotions.size(), o.assignedEmotions.size());
    }
}
