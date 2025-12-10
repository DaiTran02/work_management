package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.EventHistory;
import ws.core.model.filter.EventHistoryFilter;
import ws.core.respository.EventHistoryRepositoryCustom;

@Repository
public class EventHistoryRepositoryCustomImp implements EventHistoryRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private List<Criteria> createCriteria(EventHistoryFilter eventHistoryFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(eventHistoryFilter.getEventId()!=null) {
			criteriaList.add(Criteria.where("eventId").is(eventHistoryFilter.getEventId()));
		}

		return criteriaList;
	}

	@Override
	public List<EventHistory> findAll(EventHistoryFilter eventHistoryFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(eventHistoryFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		
		List<Order> orders=new ArrayList<Order>();
		orders.add(new Order(Sort.Direction.DESC, "_id"));
		query.with(Sort.by(orders));
		
		return this.mongoTemplate.find(query, EventHistory.class);
	}

	@Override
	public long countAll(EventHistoryFilter eventHistoryFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(eventHistoryFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, EventHistory.class);
	}
	
}
