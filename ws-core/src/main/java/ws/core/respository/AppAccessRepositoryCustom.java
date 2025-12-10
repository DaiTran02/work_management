package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.AppAccess;
import ws.core.model.filter.AppAccessFilter;

public interface AppAccessRepositoryCustom {
	List<AppAccess> findAll(AppAccessFilter appAccessFilter);
	long countAll(AppAccessFilter appAccessFilter);
	Optional<AppAccess> findOne(AppAccessFilter appAccessFilter);
}
