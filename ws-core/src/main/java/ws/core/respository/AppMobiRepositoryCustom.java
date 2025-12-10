package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;

public interface AppMobiRepositoryCustom {
	List<AppMobi> findAll(AppMobiFilter appMobiFilter);
	long countAll(AppMobiFilter appMobiFilter);
	Optional<AppMobi> findOne(AppMobiFilter appMobiFilter);
}
