package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.LogAccess;

public interface LogAccessRepository extends MongoRepository<LogAccess, ObjectId>{

}
