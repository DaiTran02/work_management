package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.TaskHistory;

public interface TaskHistoryRepository extends MongoRepository<TaskHistory, ObjectId>{

}
