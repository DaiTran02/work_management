package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Tag;

public interface TagRepository extends MongoRepository<Tag, ObjectId>{
	
}
