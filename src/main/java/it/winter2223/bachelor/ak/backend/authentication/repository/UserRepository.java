package it.winter2223.bachelor.ak.backend.authentication.repository;

import it.winter2223.bachelor.ak.backend.authentication.persistence.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
