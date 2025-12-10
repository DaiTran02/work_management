package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.TaskHistory;
import ws.core.model.filter.TaskHistoryFilter;
import ws.core.respository.TaskHistoryRepositoryCustom;

@Repository
public class TaskHistoryRepositoryCustomImpl implements TaskHistoryRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(TaskHistoryFilter taskHistoryFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(taskHistoryFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"deviceName","username","fullName"};
			for (String key : listKeys) {
				if(taskHistoryFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(taskHistoryFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+taskHistoryFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(taskHistoryFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.parseBoolean(taskHistoryFilter.getActive())));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<TaskHistory> findAll(TaskHistoryFilter taskHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(taskHistoryFilter!=null && taskHistoryFilter.getSkipLimitFilter()!=null) {
			query.skip(taskHistoryFilter.getSkipLimitFilter().getSkip());
			query.limit(taskHistoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(taskHistoryFilter!=null && taskHistoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(taskHistoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, TaskHistory.class);
	}

	@Override
	public long countAll(TaskHistoryFilter taskHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, TaskHistory.class);
	}

	@Override
	public Optional<TaskHistory> findOne(TaskHistoryFilter taskHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(taskHistoryFilter!=null && taskHistoryFilter.getSkipLimitFilter()!=null) {
			query.skip(taskHistoryFilter.getSkipLimitFilter().getSkip());
			query.limit(taskHistoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(taskHistoryFilter!=null && taskHistoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(taskHistoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, TaskHistory.class));
	}

}
