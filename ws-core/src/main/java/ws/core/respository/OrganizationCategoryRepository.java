package ws.core.respository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.OrganizationCategory;

public interface OrganizationCategoryRepository extends MongoRepository<OrganizationCategory, ObjectId>{

}
