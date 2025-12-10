package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import ws.core.model.Tag;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.model.filter.TagFilter;
import ws.core.respository.TagRepositoryCustom;

@Repository
public class TagRepositoryCustomImpl implements TagRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private Query createCriteria(TagFilter tagFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(tagFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(tagFilter.getActive().booleanValue()));
		}
		
		if(tagFilter.getArchive()!=null) {
			criteriaList.add(Criteria.where("archive").is(tagFilter.getArchive().booleanValue()));
		}
		
		if(tagFilter.getType()!=null) {
			criteriaList.add(Criteria.where("type").is(tagFilter.getType()));
		}
		
		if(tagFilter.getClassIds()!=null) {
			criteriaList.add(Criteria.where("classIds").in(tagFilter.getClassIds()));
		}
		
		if(tagFilter.getCreatorFilter()!=null) {
			if(tagFilter.getCreatorFilter().getOrganizationId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationId").is(tagFilter.getCreatorFilter().getOrganizationId()));
			}
			
			if(tagFilter.getCreatorFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationUserId").is(tagFilter.getCreatorFilter().getOrganizationUserId()));
			}
		}
		
		Query query = new Query();
		if(tagFilter.getKeySearch()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(tagFilter.getKeySearch());
			if(tagFilter.getSearchingTypeFilter()!=null) {
				if(tagFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(tagFilter.getKeySearch());
				}else if(tagFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(tagFilter.getKeySearch());
				}
			}
			query=TextQuery.queryText(criteria).sortByScore();
		}
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		for (Criteria criteria : criteriaList) {
			System.out.println(criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		return query;
	}
	
	@Override
	public List<Tag> findAll(TagFilter tagFilter) {
		Query query = createCriteria(tagFilter);

		if(tagFilter!=null && tagFilter.getSkipLimitFilter()!=null) {
			query.skip(tagFilter.getSkipLimitFilter().getSkip());
			query.limit(tagFilter.getSkipLimitFilter().getLimit());
		}
		
		if(tagFilter!=null && tagFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(tagFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Tag.class);
	}

	@Override
	public long countAll(TagFilter tagFilter) {
		Query query = createCriteria(tagFilter);
		return this.mongoTemplate.count(query, Tag.class);
	}

	@Override
	public Optional<Tag> findOne(TagFilter tagFilter) {
		Query query = createCriteria(tagFilter);
		
		if(tagFilter!=null && tagFilter.getSkipLimitFilter()!=null) {
			query.skip(tagFilter.getSkipLimitFilter().getSkip());
			query.limit(tagFilter.getSkipLimitFilter().getLimit());
		}
		
		if(tagFilter!=null && tagFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(tagFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Tag.class));
	}

}
