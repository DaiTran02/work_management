package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.enums.ConfigurationKey;
import ws.core.model.Configuration;
import ws.core.model.filter.ConfigurationFilter;
import ws.core.model.request.ReqConfigurationUpdate;
import ws.core.respository.ConfigurationRepository;
import ws.core.respository.ConfigurationRepositoryCustom;
import ws.core.services.ConfigurationService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{

	@Autowired
	private ConfigurationRepository configurationRepository;
	
	@Autowired
	private ConfigurationRepositoryCustom configurationRepositoryCustom;
	
	@Override
	public List<Configuration> findAll(ConfigurationFilter configurationFilter){
		return configurationRepositoryCustom.findAll(configurationFilter);
	}
	
	@Override
	public long countAll(ConfigurationFilter configurationFilter){
		return configurationRepositoryCustom.countAll(configurationFilter);
	}
	
	@Override
	public Optional<Configuration> findOne(ConfigurationFilter configurationFilter){
		return configurationRepositoryCustom.findOne(configurationFilter);
	}
	
	@Override
	public Optional<Configuration> findByKey(String key){
		return configurationRepository.findByKey(key);
	}
	
	@Override
	public Configuration getByKey(String key){
		Optional<Configuration> findName=findByKey(key);
		if(findName.isPresent()) {
			return findName.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tồn tại cấu hình ["+key+"]");
	}
	
	@Override
	public boolean isExistsKey(String key) {
		Optional<Configuration> findName=findByKey(key);
		return findName.isPresent();
	}
	
	@Override
	public Configuration save(Configuration configuration) {
		return configurationRepository.save(configuration);
	}
	
	@Override
	public String getValueOfKey(String key) {
		Optional<Configuration> findConfiguration = findByKey(key);
		if(findConfiguration.isPresent()) {
			return findConfiguration.get().getValue();
		}
		throw new NotFoundElementExceptionAdvice("Không tồn tại cấu hình ["+key+"]");
	}
	
	@Override
	public Configuration update(String name, ReqConfigurationUpdate reqConfigurationUpdate) {
		Optional<Configuration> findConfiguration = findByKey(name);
		if(findConfiguration.isPresent()) {
			Configuration configuration=findConfiguration.get();
			configuration.setValue(reqConfigurationUpdate.getValue());
			return save(configuration);
		}
		throw new NotFoundElementExceptionAdvice("Không tồn tại cấu hình ["+name+"]");
	}
	
	@Override
	public int getHourWorkStartOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.hourWorkStartOfDayEvent.name()));
	}
	
	public int getMinuteWorkStartOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.minuteWorkStartOfDayEvent.name()));
	}
	
	public int getHourLunchOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.hourLunchOfDayEvent.name()));
	}
	
	public int getMinuteLunchOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.minuteLunchOfDayEvent.name()));
	}
	
	public int getHourWorkEndOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.hourWorkEndOfDayEvent.name()));
	}
	
	public int getMinuteWorkEndOfDayEvent() {
		return Integer.parseInt(getValueOfKey(ConfigurationKey.minuteWorkEndOfDayEvent.name()));
	}
	
}
