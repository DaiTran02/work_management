package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.EventCalendarFile;
import ws.core.model.filter.EventCalendarFileFilter;
import ws.core.respository.EventCalendarFileRepositoryCustom;

@Repository
public class EventCalendarFileRepositoryCustomImp implements EventCalendarFileRepositoryCustom {
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private Query createCriteria(EventCalendarFileFilter eventCalendarFileFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(eventCalendarFileFilter.getId()!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(eventCalendarFileFilter.getId())));
		}
		
		if(eventCalendarFileFilter.getTime()>0) {
			Date time=new Date(eventCalendarFileFilter.getTime());
			criteriaList.add(Criteria.where("time").is(time));
		}
		
		if(eventCalendarFileFilter.getTrash()!=null) {
			criteriaList.add(Criteria.where("trash").is(eventCalendarFileFilter.getTrash().booleanValue()));
		}
		
		if(eventCalendarFileFilter.getCreatorFilter()!=null) {
			if(eventCalendarFileFilter.getCreatorFilter().getOrganizationId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationId").is(eventCalendarFileFilter.getCreatorFilter().getOrganizationId()));
			}
			
			if(eventCalendarFileFilter.getCreatorFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationUserId").is(eventCalendarFileFilter.getCreatorFilter().getOrganizationUserId()));
			}
		}
		
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return query;
	}

	@Override
	public List<EventCalendarFile> findAll(EventCalendarFileFilter eventCalendarFileFilter) {
		Query query = createCriteria(eventCalendarFileFilter);

		if(eventCalendarFileFilter!=null && eventCalendarFileFilter.getSkipLimitFilter()!=null) {
			query.skip(eventCalendarFileFilter.getSkipLimitFilter().getSkip());
			query.limit(eventCalendarFileFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventCalendarFileFilter!=null && eventCalendarFileFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventCalendarFileFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, EventCalendarFile.class);
	}

	@Override
	public long countAll(EventCalendarFileFilter eventCalendarFileFilter) {
		Query query = createCriteria(eventCalendarFileFilter);
		return (int) this.mongoTemplate.count(query, EventCalendarFile.class);
	}

	@Override
	public Optional<EventCalendarFile> findOne(EventCalendarFileFilter eventCalendarFileFilter) {
		Query query = createCriteria(eventCalendarFileFilter);
		
		if(eventCalendarFileFilter!=null && eventCalendarFileFilter.getSkipLimitFilter()!=null) {
			query.skip(eventCalendarFileFilter.getSkipLimitFilter().getSkip());
			query.limit(eventCalendarFileFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventCalendarFileFilter!=null && eventCalendarFileFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventCalendarFileFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, EventCalendarFile.class));
	}
	
}
