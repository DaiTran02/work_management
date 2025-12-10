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

import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.respository.OrganizationRepositoryCustom;

@Repository
public class OrganizationRepositoryCustomImpl implements OrganizationRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(OrganizationFilter organizationFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		if(organizationFilter==null)
			return criteriaList;
		
		if(organizationFilter.getIds()!=null) {
			criteriaList.add(Criteria.where("_id").in(organizationFilter.getIds()));
		}
		
		if(organizationFilter.getUnitCodes()!=null) {
			criteriaList.add(Criteria.where("unitCode").in(organizationFilter.getUnitCodes()));
		}
		
		if(organizationFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"name","description","unitCode"};
			for (String key : listKeys) {
				if(organizationFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(organizationFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+organizationFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(organizationFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(organizationFilter.getActive().booleanValue()));
		}
		
		if(organizationFilter.getParentId()!=null) {
			List<Criteria> subCriteriasOr = new ArrayList<>();
			subCriteriasOr.add(Criteria.where("parentId").is(organizationFilter.getParentId()));
			subCriteriasOr.add(Criteria.where("parentIdSeconds").in(organizationFilter.getParentId()));
			criteriaList.add(new Criteria().orOperator(subCriteriasOr.toArray(new Criteria[subCriteriasOr.size()])));
		}
		
		if(organizationFilter.isRoot()) {
			criteriaList.add(Criteria.where("parentId").exists(false));
		}
		
		if(organizationFilter.getIncludeUserId()!=null) {
			criteriaList.add(Criteria.where("userOrganizationExpands").elemMatch(Criteria.where("userId").in(organizationFilter.getIncludeUserId())));
		}
		
		/* Tìm đơn vị với user rỗng hoặc khác rỗng */
		if(organizationFilter.getHasContainUsers()!=null) {
			if(organizationFilter.getHasContainUsers().booleanValue()) {
				criteriaList.add(Criteria.where("userOrganizationExpands").size(0));
			}else {
				criteriaList.add(Criteria.where("userOrganizationExpands").size(0).not());
			}
		}
		
		if(organizationFilter.getOrganizationCategoryId()!=null) {
			criteriaList.add(Criteria.where("organizationCategoryId").is(organizationFilter.getOrganizationCategoryId()));
		}
		
		if(organizationFilter.getLevel()!=null) {
			criteriaList.add(Criteria.where("level").is(organizationFilter.getLevel()));
		}
		
		/* Tìm theo roleOrganizationExpandFilter */
		if(organizationFilter.getRoleOrganizationExpandFilter()!=null) {
			List<Criteria> andCriteriasRoleOrganizationExpand = new ArrayList<>();
			if(organizationFilter.getRoleOrganizationExpandFilter().getRoleTemplateId()!=null) {
				andCriteriasRoleOrganizationExpand.add(Criteria.where("roleTemplateId").is(organizationFilter.getRoleOrganizationExpandFilter().getRoleTemplateId()));
			}
			
			if(andCriteriasRoleOrganizationExpand.size()>0) {
				criteriaList.add(Criteria.where("roleOrganizationExpands").elemMatch(new Criteria().andOperator(andCriteriasRoleOrganizationExpand.toArray(new Criteria[andCriteriasRoleOrganizationExpand.size()]))));
			}
		}
		
		return criteriaList;
	}
	
	@Override
	public List<Organization> findAll(OrganizationFilter organizationFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(organizationFilter!=null && organizationFilter.getSkipLimitFilter()!=null) {
			query.skip(organizationFilter.getSkipLimitFilter().getSkip());
			query.limit(organizationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(organizationFilter!=null && organizationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(organizationFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Organization.class);
	}

	@Override
	public long countAll(OrganizationFilter organizationFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, Organization.class);
	}

	@Override
	public Optional<Organization> findOne(OrganizationFilter organizationFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(organizationFilter!=null && organizationFilter.getSkipLimitFilter()!=null) {
			query.skip(organizationFilter.getSkipLimitFilter().getSkip());
			query.limit(organizationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(organizationFilter!=null && organizationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(organizationFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Organization.class));
	}

}
