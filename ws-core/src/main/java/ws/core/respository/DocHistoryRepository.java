package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.DocHistory;

public interface DocHistoryRepository extends MongoRepository<DocHistory, ObjectId>{

}
