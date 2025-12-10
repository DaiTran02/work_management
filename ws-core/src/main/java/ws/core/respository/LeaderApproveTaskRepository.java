package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.LeaderApproveTask;

public interface LeaderApproveTaskRepository extends MongoRepository<LeaderApproveTask, ObjectId>{

}
