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

import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;
import ws.core.respository.LogRequestRepositoryCustom;

@Repository
public class LogRequestRepositoryCustomImpl implements LogRequestRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(LogRequestFilter logRequestFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* Tìm organizationId */
		if(logRequestFilter.getOrganizationId()!=null) {
			criteriaList.add(Criteria.where("organizationId").is(logRequestFilter.getOrganizationId()));
		}
		
		/* Tìm userId */
		if(logRequestFilter.getUserId()!=null) {
			criteriaList.add(Criteria.where("userId").is(logRequestFilter.getUserId()));
		}
		
		/* Tìm theo khung thời gian giao */
		if(logRequestFilter.getFromDate()>0 && logRequestFilter.getToDate()>0) {
			Date fromDate=new Date(logRequestFilter.getFromDate());
			Date toDate=new Date(logRequestFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(logRequestFilter.getFromDate()>0) {
			Date fromDate=new Date(logRequestFilter.getFromDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(logRequestFilter.getToDate()>0) {
			Date toDate=new Date(logRequestFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		if(logRequestFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"access","userAgent"};
			for (String key : listKeys) {
				if(logRequestFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(logRequestFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+logRequestFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		return criteriaList;
	}
	
	public List<LogRequest> findAll(LogRequestFilter logRequestFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logRequestFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(logRequestFilter!=null && logRequestFilter.getSkipLimitFilter()!=null) {
			query.skip(logRequestFilter.getSkipLimitFilter().getSkip());
			query.limit(logRequestFilter.getSkipLimitFilter().getLimit());
		}
		
		if(logRequestFilter!=null && logRequestFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(logRequestFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, LogRequest.class);
	}

	public long countAll(LogRequestFilter logRequestFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logRequestFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, LogRequest.class);
	}

	@Override
	public Optional<LogRequest> findOne(LogRequestFilter logRequestFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(logRequestFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(logRequestFilter!=null && logRequestFilter.getSkipLimitFilter()!=null) {
			query.skip(logRequestFilter.getSkipLimitFilter().getSkip());
			query.limit(logRequestFilter.getSkipLimitFilter().getLimit());
		}
		
		if(logRequestFilter!=null && logRequestFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(logRequestFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, LogRequest.class));
	}

}
