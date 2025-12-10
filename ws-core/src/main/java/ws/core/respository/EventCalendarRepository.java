package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.EventCalendar;

public interface EventCalendarRepository extends MongoRepository<EventCalendar, ObjectId>{

}
