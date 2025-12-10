package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.User;
import ws.core.model.filter.UserFilter;

public interface UserRepositoryCustom {
	List<User> findAll(UserFilter userFilter);
	long countAll(UserFilter userFilter);
	boolean checkPassword(String userId, String password);
	Optional<User> findOne(UserFilter userFilter);
}
