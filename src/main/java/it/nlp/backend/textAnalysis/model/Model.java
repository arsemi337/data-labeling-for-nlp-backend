package it.nlp.backend.textAnalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Model {

    @Id
    private String name;
    private String modelInfo;
}
