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

import ws.core.model.AppAccess;
import ws.core.model.filter.AppAccessFilter;
import ws.core.respository.AppAccessRepositoryCustom;

@Repository
public class AppAccessRepositoryCustomImpl implements AppAccessRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(AppAccessFilter appAccessFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(appAccessFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name","description","creatorName","ipsAccess","apiKey"};
			for (String key : listKeys) {
				if(appAccessFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(appAccessFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+appAccessFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(appAccessFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.parseBoolean(appAccessFilter.getActive())));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<AppAccess> findAll(AppAccessFilter appAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(appAccessFilter!=null && appAccessFilter.getSkipLimitFilter()!=null) {
			query.skip(appAccessFilter.getSkipLimitFilter().getSkip());
			query.limit(appAccessFilter.getSkipLimitFilter().getLimit());
		}
		
		if(appAccessFilter!=null && appAccessFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(appAccessFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, AppAccess.class);
	}

	@Override
	public long countAll(AppAccessFilter appAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, AppAccess.class);
	}

	@Override
	public Optional<AppAccess> findOne(AppAccessFilter appAccessFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appAccessFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(appAccessFilter!=null && appAccessFilter.getSkipLimitFilter()!=null) {
			query.skip(appAccessFilter.getSkipLimitFilter().getSkip());
			query.limit(appAccessFilter.getSkipLimitFilter().getLimit());
		}
		
		if(appAccessFilter!=null && appAccessFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(appAccessFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, AppAccess.class));
	}

}
