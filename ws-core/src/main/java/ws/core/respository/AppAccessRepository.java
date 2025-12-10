package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.AppAccess;

public interface AppAccessRepository extends MongoRepository<AppAccess, ObjectId>{
	Optional<AppAccess> findByApiKey(String apiKey);
}
