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

import ws.core.model.LeaderApproveTask;
import ws.core.model.filter.LeaderApproveTaskFilter;
import ws.core.respository.LeaderApproveTaskRepositoryCustom;

@Repository
public class LeaderApproveTaskRepositoryCustomImpl implements LeaderApproveTaskRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(LeaderApproveTaskFilter leaderApproveTaskFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(leaderApproveTaskFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name"};
			for (String key : listKeys) {
				if(leaderApproveTaskFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(leaderApproveTaskFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+leaderApproveTaskFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(leaderApproveTaskFilter.getOrganizationId()!=null) {
			criteriaList.add(Criteria.where("organizationId").is(leaderApproveTaskFilter.getOrganizationId()));
		} 
		
		if(leaderApproveTaskFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(leaderApproveTaskFilter.getActive().booleanValue()));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<LeaderApproveTask> findAll(LeaderApproveTaskFilter leaderApproveTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(leaderApproveTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(leaderApproveTaskFilter!=null && leaderApproveTaskFilter.getSkipLimitFilter()!=null) {
			query.skip(leaderApproveTaskFilter.getSkipLimitFilter().getSkip());
			query.limit(leaderApproveTaskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(leaderApproveTaskFilter!=null && leaderApproveTaskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(leaderApproveTaskFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, LeaderApproveTask.class);
	}

	@Override
	public long countAll(LeaderApproveTaskFilter leaderApproveTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(leaderApproveTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, LeaderApproveTask.class);
	}

	@Override
	public Optional<LeaderApproveTask> findOne(LeaderApproveTaskFilter leaderApproveTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(leaderApproveTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(leaderApproveTaskFilter!=null && leaderApproveTaskFilter.getSkipLimitFilter()!=null) {
			query.skip(leaderApproveTaskFilter.getSkipLimitFilter().getSkip());
			query.limit(leaderApproveTaskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(leaderApproveTaskFilter!=null && leaderApproveTaskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(leaderApproveTaskFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, LeaderApproveTask.class));
	}

}
