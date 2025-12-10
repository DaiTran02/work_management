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

import ws.core.model.DocHistory;
import ws.core.model.filter.DocHistoryFilter;
import ws.core.respository.DocHistoryRepositoryCustom;

@Repository
public class DocHistoryRepositoryCustomImpl implements DocHistoryRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(DocHistoryFilter docHistoryFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(docHistoryFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"deviceName","username","fullName"};
			for (String key : listKeys) {
				if(docHistoryFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(docHistoryFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+docHistoryFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(docHistoryFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.parseBoolean(docHistoryFilter.getActive())));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<DocHistory> findAll(DocHistoryFilter docHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(docHistoryFilter!=null && docHistoryFilter.getSkipLimitFilter()!=null) {
			query.skip(docHistoryFilter.getSkipLimitFilter().getSkip());
			query.limit(docHistoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(docHistoryFilter!=null && docHistoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(docHistoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, DocHistory.class);
	}

	@Override
	public long countAll(DocHistoryFilter docHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, DocHistory.class);
	}

	@Override
	public Optional<DocHistory> findOne(DocHistoryFilter docHistoryFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docHistoryFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(docHistoryFilter!=null && docHistoryFilter.getSkipLimitFilter()!=null) {
			query.skip(docHistoryFilter.getSkipLimitFilter().getSkip());
			query.limit(docHistoryFilter.getSkipLimitFilter().getLimit());
		}
		
		if(docHistoryFilter!=null && docHistoryFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(docHistoryFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, DocHistory.class));
	}

}
