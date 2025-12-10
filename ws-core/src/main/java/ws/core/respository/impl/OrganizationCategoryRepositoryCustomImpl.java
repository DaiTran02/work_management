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

import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrganizationCategoryFilter;
import ws.core.respository.OrganizationCategoryRepositoryCustom;

@Repository
public class OrganizationCategoryRepositoryCustomImpl implements OrganizationCategoryRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(OrganizationCategoryFilter organizationCategoryFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(organizationCategoryFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name","description"};
			for (String key : listKeys) {
				if(organizationCategoryFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(organizationCategoryFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+organizationCategoryFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(organizationCategoryFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(organizationCategoryFilter.getActive().booleanValue()));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<OrganizationCategory> findAll(OrganizationCategoryFilter organizationCategoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationCategoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(organizationCategoryFilter!=null && organizationCategoryFilter.getSkipLimitFilter()!=null) {
			query.skip(organizationCategoryFilter.getSkipLimitFilter().getSkip());
			query.limit(organizationCategoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(organizationCategoryFilter!=null && organizationCategoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(organizationCategoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, OrganizationCategory.class);
	}

	@Override
	public long countAll(OrganizationCategoryFilter organizationCategoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationCategoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, OrganizationCategory.class);
	}

	@Override
	public Optional<OrganizationCategory> findOne(OrganizationCategoryFilter organizationCategoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationCategoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(organizationCategoryFilter!=null && organizationCategoryFilter.getSkipLimitFilter()!=null) {
			query.skip(organizationCategoryFilter.getSkipLimitFilter().getSkip());
			query.limit(organizationCategoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(organizationCategoryFilter!=null && organizationCategoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(organizationCategoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, OrganizationCategory.class));
	}

}
