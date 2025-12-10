package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.ClassifyTask;
import ws.core.model.filter.ClassifyTaskFilter;

public interface ClassifyTaskRepositoryCustom {
	List<ClassifyTask> findAll(ClassifyTaskFilter classifyTaskFilter);
	long countAll(ClassifyTaskFilter classifyTaskFilter);
	Optional<ClassifyTask> findOne(ClassifyTaskFilter classifyTaskFilter);
}
