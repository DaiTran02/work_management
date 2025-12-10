package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.LogAccess;
import ws.core.model.filter.LogAccessFilter;
import ws.core.respository.LogAccessRepositoryCustom;

@Repository
public class LogAccessRepositoryCustomImpl implements LogAccessRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(LogAccessFilter logAccessFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* Tìm organizationId */
		if(logAccessFilter.getOrganizationId()!=null) {
			criteriaList.add(Criteria.where("organizationId").is(logAccessFilter.getOrganizationId()));
		}
		
		/* Tìm userId */
		if(logAccessFilter.getUserId()!=null) {
			criteriaList.add(Criteria.where("userId").is(logAccessFilter.getUserId()));
		}
		
		/* Tìm theo khung thời gian giao */
		if(logAccessFilter.getFromDate()>0 && logAccessFilter.getToDate()>0) {
			Date fromDate=new Date(logAccessFilter.getFromDate());
			Date toDate=new Date(logAccessFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(logAccessFilter.getFromDate()>0) {
			Date fromDate=new Date(logAccessFilter.getFromDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(logAccessFilter.getToDate()>0) {
			Date toDate=new Date(logAccessFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		if(logAccessFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"access","userAgent"};
			for (String key : listKeys) {
				if(logAccessFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(logAccessFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+logAccessFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		return criteriaList;
	}
	
	public List<LogAccess> findAll(LogAccessFilter logAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(logAccessFilter!=null && logAccessFilter.getSkipLimitFilter()!=null) {
			query.skip(logAccessFilter.getSkipLimitFilter().getSkip());
			query.limit(logAccessFilter.getSkipLimitFilter().getLimit());
		}
		
		if(logAccessFilter!=null && logAccessFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(logAccessFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, LogAccess.class);
	}

	public long countAll(LogAccessFilter logAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, LogAccess.class);
	}

	@Override
	public List<String> getDistinctUsers(LogAccessFilter logAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.findDistinct(query, "userId", LogAccess.class, String.class);
	}

	@Override
	public Optional<LogAccess> findOne(LogAccessFilter logAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(logAccessFilter!=null && logAccessFilter.getSkipLimitFilter()!=null) {
			query.skip(logAccessFilter.getSkipLimitFilter().getSkip());
			query.limit(logAccessFilter.getSkipLimitFilter().getLimit());
		}
		
		if(logAccessFilter!=null && logAccessFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(logAccessFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, LogAccess.class));
	}

}
