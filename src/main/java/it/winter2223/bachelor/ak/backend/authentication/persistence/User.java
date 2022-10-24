package it.winter2223.bachelor.ak.backend.authentication.persistence;

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
public class User {
    @Id
    private final UUID userId;

    private final String login;

    private final String password;

    private final String token;
}
