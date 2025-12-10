package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.respository.MediaRepositoryCustom;

@Repository
public class MediaRepositoryCustomImp implements MediaRepositoryCustom {
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private Query createCriteria(MediaFilter mediaFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(mediaFilter.getId()!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(mediaFilter.getId())));
		}
		
		if(mediaFilter.getIds()!=null && mediaFilter.getIds().size()>0) {
			criteriaList.add(Criteria.where("_id").in(mediaFilter.getIds()));
		}
		
		Query query = new Query();
		if(mediaFilter.getKeyword()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(mediaFilter.getKeyword());
			if(mediaFilter.getSearchingTypeFilter()!=null) {
				if(mediaFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(mediaFilter.getKeyword());
				}else if(mediaFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(mediaFilter.getKeyword());
				}
			}
			query=TextQuery.queryText(criteria).sortByScore();
		}
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		return query;
	}

	@Override
	public List<Media> findAll(MediaFilter mediaFilter) {
		Query query = createCriteria(mediaFilter);

		if(mediaFilter!=null && mediaFilter.getSkipLimitFilter()!=null) {
			query.skip(mediaFilter.getSkipLimitFilter().getSkip());
			query.limit(mediaFilter.getSkipLimitFilter().getLimit());
		}
		
		if(mediaFilter!=null && mediaFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(mediaFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Media.class);
	}

	@Override
	public long countAll(MediaFilter mediaFilter) {
		Query query = createCriteria(mediaFilter);
		return (int) this.mongoTemplate.count(query, Media.class);
	}

	@Override
	public Optional<Media> findOne(MediaFilter mediaFilter) {
		Query query = createCriteria(mediaFilter);
		
		if(mediaFilter!=null && mediaFilter.getSkipLimitFilter()!=null) {
			query.skip(mediaFilter.getSkipLimitFilter().getSkip());
			query.limit(mediaFilter.getSkipLimitFilter().getLimit());
		}
		
		if(mediaFilter!=null && mediaFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(mediaFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Media.class));
	}
	
}
