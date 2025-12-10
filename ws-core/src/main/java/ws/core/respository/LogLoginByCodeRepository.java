package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.LogLoginByCode;

public interface LogLoginByCodeRepository extends MongoRepository<LogLoginByCode, ObjectId>{

}
