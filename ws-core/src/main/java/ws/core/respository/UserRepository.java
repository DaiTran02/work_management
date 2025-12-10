package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.User;

public interface UserRepository extends MongoRepository<User, ObjectId>{
	Optional<User> findByUsername(String username);
}
