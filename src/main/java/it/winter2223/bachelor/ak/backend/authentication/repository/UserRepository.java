package it.winter2223.bachelor.ak.backend.authentication.repository;

import it.winter2223.bachelor.ak.backend.authentication.persistence.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {

    Optional<User> findByLoginAndPassword(UUID uuid);
}
