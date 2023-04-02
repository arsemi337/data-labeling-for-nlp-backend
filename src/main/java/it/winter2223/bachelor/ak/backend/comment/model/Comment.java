package it.winter2223.bachelor.ak.backend.comment.model;

import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.CANNOT_COMPARE_NULL_COMMENT;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment implements Comparable<Comment> {
    @Id
    private String commentId;

    private String content;

    private Integer assignmentsNumber;

    public void increaseAssignmentsNumber() {
        assignmentsNumber++;
    }

    @Override
    public int compareTo( Comment o) {
        if (o == null) {
            throw new CommentException(CANNOT_COMPARE_NULL_COMMENT.getMessage());
        }
        return assignmentsNumber.compareTo(o.getAssignmentsNumber());
    }
}
