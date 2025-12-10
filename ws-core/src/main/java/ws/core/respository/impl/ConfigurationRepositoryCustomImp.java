package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import ws.core.model.Configuration;
import ws.core.model.filter.ConfigurationFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.respository.ConfigurationRepositoryCustom;

@Repository
public class ConfigurationRepositoryCustomImp implements ConfigurationRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private Query createCriteria(ConfigurationFilter configurationFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(configurationFilter.getId()!=null) {
			criteriaList.add(Criteria.where("_id").is(configurationFilter.getId()));
		}
		
		if(configurationFilter.getKey()!=null) {
			criteriaList.add(Criteria.where("key").is(configurationFilter.getKey()));
		}
		
		if(configurationFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(configurationFilter.getActive().booleanValue()));
		}
		
		Query query = new Query();
		if(configurationFilter.getKeySearch()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(configurationFilter.getKeySearch());
			if(configurationFilter.getSearchingTypeFilter()!=null) {
				if(configurationFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(configurationFilter.getKeySearch());
				}else if(configurationFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(configurationFilter.getKeySearch());
				}
			}
			query=TextQuery.queryText(criteria).sortByScore();
		}
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return query;
	}

	@Override
	public List<Configuration> findAll(ConfigurationFilter configurationFilter) {
		Query query = createCriteria(configurationFilter);

		if(configurationFilter!=null && configurationFilter.getSkipLimitFilter()!=null) {
			query.skip(configurationFilter.getSkipLimitFilter().getSkip());
			query.limit(configurationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(configurationFilter!=null && configurationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(configurationFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Configuration.class);
	}

	@Override
	public long countAll(ConfigurationFilter configurationFilter) {
		Query query = createCriteria(configurationFilter);
		return this.mongoTemplate.count(query, Configuration.class);
	}

	@Override
	public Optional<Configuration> findOne(ConfigurationFilter configurationFilter) {
		Query query = createCriteria(configurationFilter);
		
		if(configurationFilter!=null && configurationFilter.getSkipLimitFilter()!=null) {
			query.skip(configurationFilter.getSkipLimitFilter().getSkip());
			query.limit(configurationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(configurationFilter!=null && configurationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(configurationFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Configuration.class));
	}
	
}
