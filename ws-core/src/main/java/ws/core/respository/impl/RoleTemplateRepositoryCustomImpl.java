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

import ws.core.model.RoleTemplate;
import ws.core.model.filter.RoleTemplateFilter;
import ws.core.respository.RoleTemplateRepositoryCustom;

@Repository
public class RoleTemplateRepositoryCustomImpl implements RoleTemplateRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(RoleTemplateFilter roleTemplateFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(roleTemplateFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name","description"};
			for (String key : listKeys) {
				if(roleTemplateFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(roleTemplateFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+roleTemplateFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(roleTemplateFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.parseBoolean(roleTemplateFilter.getActive())));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<RoleTemplate> findAll(RoleTemplateFilter roleTemplateFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(roleTemplateFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(roleTemplateFilter!=null && roleTemplateFilter.getSkipLimitFilter()!=null) {
			query.skip(roleTemplateFilter.getSkipLimitFilter().getSkip());
			query.limit(roleTemplateFilter.getSkipLimitFilter().getLimit());
		}
		
		if(roleTemplateFilter!=null && roleTemplateFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(roleTemplateFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, RoleTemplate.class);
	}

	@Override
	public long countAll(RoleTemplateFilter roleTemplateFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(roleTemplateFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, RoleTemplate.class);
	}

	@Override
	public Optional<RoleTemplate> findOne(RoleTemplateFilter roleTemplateFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(roleTemplateFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(roleTemplateFilter!=null && roleTemplateFilter.getSkipLimitFilter()!=null) {
			query.skip(roleTemplateFilter.getSkipLimitFilter().getSkip());
			query.limit(roleTemplateFilter.getSkipLimitFilter().getLimit());
		}
		
		if(roleTemplateFilter!=null && roleTemplateFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(roleTemplateFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, RoleTemplate.class));
	}

}
