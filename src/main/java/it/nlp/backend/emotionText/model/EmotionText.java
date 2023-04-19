package it.nlp.backend.emotionText.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static it.nlp.backend.exception.messages.TextExceptionMessages.CANNOT_COMPARE_NULL_TEXT;

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
            throw new NullPointerException(CANNOT_COMPARE_NULL_TEXT.getMessage());
        }
        return Integer.compare(assignedEmotions.size(), o.assignedEmotions.size());
    }
}