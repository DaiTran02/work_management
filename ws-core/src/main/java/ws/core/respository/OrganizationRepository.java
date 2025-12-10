package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Organization;

public interface OrganizationRepository extends MongoRepository<Organization, ObjectId>{
	public Optional<Organization> findByUnitCode(String unitCode);
}
