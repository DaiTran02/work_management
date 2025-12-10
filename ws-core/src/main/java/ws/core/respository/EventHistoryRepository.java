package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.EventHistory;

public interface EventHistoryRepository extends MongoRepository<EventHistory, ObjectId>{

}
