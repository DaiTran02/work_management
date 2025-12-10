package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.EventResource;

public interface EventResourceRepository extends MongoRepository<EventResource, ObjectId>{

}
