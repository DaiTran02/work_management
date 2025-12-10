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

import ws.core.model.ClassifyTask;
import ws.core.model.filter.ClassifyTaskFilter;
import ws.core.respository.ClassifyTaskRepositoryCustom;

@Repository
public class ClassifyTaskRepositoryCustomImpl implements ClassifyTaskRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(ClassifyTaskFilter classifyTaskFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(classifyTaskFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name"};
			for (String key : listKeys) {
				if(classifyTaskFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(classifyTaskFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+classifyTaskFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(classifyTaskFilter.getOrganizationId()!=null) {
			criteriaList.add(Criteria.where("organizationId").is(classifyTaskFilter.getOrganizationId()));
		}
		
		if(classifyTaskFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(classifyTaskFilter.getActive().booleanValue()));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<ClassifyTask> findAll(ClassifyTaskFilter classifyTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(classifyTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(classifyTaskFilter!=null && classifyTaskFilter.getSkipLimitFilter()!=null) {
			query.skip(classifyTaskFilter.getSkipLimitFilter().getSkip());
			query.limit(classifyTaskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(classifyTaskFilter!=null && classifyTaskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(classifyTaskFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, ClassifyTask.class);
	}

	@Override
	public long countAll(ClassifyTaskFilter classifyTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(classifyTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, ClassifyTask.class);
	}

	@Override
	public Optional<ClassifyTask> findOne(ClassifyTaskFilter classifyTaskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(classifyTaskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(classifyTaskFilter!=null && classifyTaskFilter.getSkipLimitFilter()!=null) {
			query.skip(classifyTaskFilter.getSkipLimitFilter().getSkip());
			query.limit(classifyTaskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(classifyTaskFilter!=null && classifyTaskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(classifyTaskFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, ClassifyTask.class));
	}

}
