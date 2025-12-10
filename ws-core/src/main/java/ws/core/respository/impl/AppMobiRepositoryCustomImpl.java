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

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;
import ws.core.respository.AppMobiRepositoryCustom;

@Repository
public class AppMobiRepositoryCustomImpl implements AppMobiRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(AppMobiFilter appMobiFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(appMobiFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"deviceName","username","fullName"};
			for (String key : listKeys) {
				if(appMobiFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(appMobiFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+appMobiFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(appMobiFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.parseBoolean(appMobiFilter.getActive())));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<AppMobi> findAll(AppMobiFilter appMobiFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appMobiFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(appMobiFilter!=null && appMobiFilter.getSkipLimitFilter()!=null) {
			query.skip(appMobiFilter.getSkipLimitFilter().getSkip());
			query.limit(appMobiFilter.getSkipLimitFilter().getLimit());
		}
		
		if(appMobiFilter!=null && appMobiFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(appMobiFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, AppMobi.class);
	}

	@Override
	public long countAll(AppMobiFilter appMobiFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appMobiFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, AppMobi.class);
	}

	@Override
	public Optional<AppMobi> findOne(AppMobiFilter appMobiFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(appMobiFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(appMobiFilter!=null && appMobiFilter.getSkipLimitFilter()!=null) {
			query.skip(appMobiFilter.getSkipLimitFilter().getSkip());
			query.limit(appMobiFilter.getSkipLimitFilter().getLimit());
		}
		
		if(appMobiFilter!=null && appMobiFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(appMobiFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, AppMobi.class));
	}

}
