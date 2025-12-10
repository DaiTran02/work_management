package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Doc;

public interface DocRepository extends MongoRepository<Doc, ObjectId>{
	public Optional<Doc> findByiOfficeId(String iofficeId);
}
