package ws.core.respository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, ObjectId>{
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
