package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.PersonalRecord;

public interface PersonalRecordRepository extends MongoRepository<PersonalRecord, ObjectId>{

}
