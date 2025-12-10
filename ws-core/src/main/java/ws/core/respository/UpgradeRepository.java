package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Upgrade;

public interface UpgradeRepository extends MongoRepository<Upgrade, ObjectId>{
	Optional<Upgrade> findByName(String name);
}
