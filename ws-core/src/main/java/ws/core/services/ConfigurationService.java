package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.model.Configuration;
import ws.core.model.filter.ConfigurationFilter;
import ws.core.model.request.ReqConfigurationUpdate;

public interface ConfigurationService {
	
	public List<Configuration> findAll(ConfigurationFilter configurationFilter);
	
	public long countAll(ConfigurationFilter configurationFilter);
	
	public Optional<Configuration> findOne(ConfigurationFilter configurationFilter);
	
	public Optional<Configuration> findByKey(String key);
	
	public Configuration getByKey(String key);
	
	public boolean isExistsKey(String key);
	
	public Configuration save(Configuration configuration);
	
	public String getValueOfKey(String key);
	
	public Configuration update(String name, ReqConfigurationUpdate reqConfigurationUpdate);
	
	public int getHourWorkStartOfDayEvent();
	
	public int getMinuteWorkStartOfDayEvent();
	
	public int getHourLunchOfDayEvent();
	
	public int getMinuteLunchOfDayEvent();
	
	public int getHourWorkEndOfDayEvent();
	
	public int getMinuteWorkEndOfDayEvent();
}
