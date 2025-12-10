package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Media;

public interface MediaRepository extends MongoRepository<Media, ObjectId>{

}
