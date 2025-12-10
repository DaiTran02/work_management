package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.ClassifyTask;

public interface ClassifyTaskRepository extends MongoRepository<ClassifyTask, ObjectId>{

}
