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

import ws.core.model.Permission;
import ws.core.model.filter.PermissionFilter;
import ws.core.respository.PermissionRepositoryCustom;

@Repository
public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(PermissionFilter permissionFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		return criteriaList;
	}
	
	@Override
	public List<Permission> findAll(PermissionFilter permissionFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(permissionFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(permissionFilter!=null && permissionFilter.getSkipLimitFilter()!=null) {
			query.skip(permissionFilter.getSkipLimitFilter().getSkip());
			query.limit(permissionFilter.getSkipLimitFilter().getLimit());
		}
		
		if(permissionFilter!=null && permissionFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(permissionFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Permission.class);
	}

	@Override
	public long countAll(PermissionFilter permissionFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(permissionFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, Permission.class);
	}

	@Override
	public Optional<Permission> findOne(PermissionFilter permissionFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(permissionFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(permissionFilter!=null && permissionFilter.getSkipLimitFilter()!=null) {
			query.skip(permissionFilter.getSkipLimitFilter().getSkip());
			query.limit(permissionFilter.getSkipLimitFilter().getLimit());
		}
		
		if(permissionFilter!=null && permissionFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(permissionFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Permission.class));
	}

}
