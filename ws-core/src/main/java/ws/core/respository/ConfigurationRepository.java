package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Configuration;

public interface ConfigurationRepository extends MongoRepository<Configuration, ObjectId>{
	public Optional<Configuration> findByKey(String key);
}
