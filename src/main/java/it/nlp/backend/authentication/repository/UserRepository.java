package it.nlp.backend.authentication.repository;

import it.nlp.backend.authentication.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}
