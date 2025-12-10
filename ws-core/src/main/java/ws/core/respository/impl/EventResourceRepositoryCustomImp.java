package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import ws.core.model.EventResource;
import ws.core.model.filter.EventResourceFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.respository.EventResourceRepositoryCustom;

@Repository
public class EventResourceRepositoryCustomImp implements EventResourceRepositoryCustom {
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private Query createCriteria(EventResourceFilter eventResourceFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(eventResourceFilter.getId()!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(eventResourceFilter.getId())));
		}
		
		if(eventResourceFilter.getIds()!=null && eventResourceFilter.getIds().size()>0) {
			criteriaList.add(Criteria.where("_id").in(eventResourceFilter.getIds()));
		}
		
		if(eventResourceFilter.getCreatorFilter()!=null) {
			if(eventResourceFilter.getCreatorFilter().getOrganizationId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationId").is(eventResourceFilter.getCreatorFilter().getOrganizationId()));
			}
			
			if(eventResourceFilter.getCreatorFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationUserId").is(eventResourceFilter.getCreatorFilter().getOrganizationUserId()));
			}
		}
		
		if(eventResourceFilter.getGroup()!=null) {
			criteriaList.add(Criteria.where("group").is(eventResourceFilter.getGroup().intValue()));
		}
		
		if(eventResourceFilter.getTrash()!=null) {
			criteriaList.add(Criteria.where("trash").is(eventResourceFilter.getTrash().booleanValue()));
		}
		
		if(eventResourceFilter.getType()!=null) {
			criteriaList.add(Criteria.where("type").is(eventResourceFilter.getType()));
		}
		
		Query query = new Query();
		if(eventResourceFilter.getKeyword()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(eventResourceFilter.getKeyword());
			if(eventResourceFilter.getSearchingTypeFilter()!=null) {
				if(eventResourceFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(eventResourceFilter.getKeyword());
				}else if(eventResourceFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(eventResourceFilter.getKeyword());
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
	public List<EventResource> findAll(EventResourceFilter eventResourceFilter) {
		Query query = createCriteria(eventResourceFilter);

		if(eventResourceFilter!=null && eventResourceFilter.getSkipLimitFilter()!=null) {
			query.skip(eventResourceFilter.getSkipLimitFilter().getSkip());
			query.limit(eventResourceFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventResourceFilter!=null && eventResourceFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventResourceFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, EventResource.class);
	}

	@Override
	public long countAll(EventResourceFilter eventResourceFilter) {
		Query query = createCriteria(eventResourceFilter);
		return (int) this.mongoTemplate.count(query, EventResource.class);
	}

	@Override
	public Optional<EventResource> findOne(EventResourceFilter eventResourceFilter) {
		Query query = createCriteria(eventResourceFilter);
		
		if(eventResourceFilter!=null && eventResourceFilter.getSkipLimitFilter()!=null) {
			query.skip(eventResourceFilter.getSkipLimitFilter().getSkip());
			query.limit(eventResourceFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventResourceFilter!=null && eventResourceFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventResourceFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, EventResource.class));
	}
	
}
