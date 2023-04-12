package it.nlp.backend.migration.service;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment{
    @Id
    private String commentId;

    private String content;

    private Integer assignmentsNumber;

    public void increaseAssignmentsNumber() {
        assignmentsNumber++;
    }

//    @Override
//    public int compareTo(Comment o) {
//        if (o == null) {
//            throw new CommentException(CANNOT_COMPARE_NULL_COMMENT.getMessage());
//        }
//        return assignmentsNumber.compareTo(o.getAssignmentsNumber());
//    }
}
