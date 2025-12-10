package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Configuration;
import ws.core.model.filter.ConfigurationFilter;

public interface ConfigurationRepositoryCustom{
	List<Configuration> findAll(ConfigurationFilter configurationFilter);
	long countAll(ConfigurationFilter configurationFilter);
	Optional<Configuration> findOne(ConfigurationFilter configurationFilter);
}
