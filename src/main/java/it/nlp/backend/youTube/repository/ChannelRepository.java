package it.nlp.backend.youTube.repository;

import it.nlp.backend.youTube.model.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    boolean existsById(String id);
}
