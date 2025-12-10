package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId>{

}
