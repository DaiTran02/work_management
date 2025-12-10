package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.EventCalendarFile;

public interface EventCalendarFileRepository extends MongoRepository<EventCalendarFile, ObjectId>{

}
