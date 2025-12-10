package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import ws.core.model.Notification;
import ws.core.model.filter.NotificationFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.respository.NotificationRepositoryCustom;

@Repository
public class NotificationRepositoryCustomImp implements NotificationRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private Query buildQuery(NotificationFilter notificationFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(notificationFilter.getFromDate()>0 && notificationFilter.getToDate()>0) {
			Date fromDate=new Date(notificationFilter.getFromDate());
			Date toDate=new Date(notificationFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(notificationFilter.getFromDate()>0) {
			Date fromDate=new Date(notificationFilter.getFromDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(notificationFilter.getToDate()>0) {
			Date toDate=new Date(notificationFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		if(notificationFilter.getCreatorFilter()!=null) {
			if(notificationFilter.getCreatorFilter().getOrganizationIds()!=null) {
				criteriaList.add(Criteria.where("creator.organizationId").in(notificationFilter.getCreatorFilter().getOrganizationIds()));
			}else if(notificationFilter.getCreatorFilter().getOrganizationId()!=null){
				criteriaList.add(Criteria.where("creator.organizationId").is(notificationFilter.getCreatorFilter().getOrganizationId()));
			}
			
			if(notificationFilter.getCreatorFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("creator.organizationUserId").is(notificationFilter.getCreatorFilter().getOrganizationUserId()));
			}
		}
		
		if(notificationFilter.getReceiverFilter()!=null) {
			if(notificationFilter.getReceiverFilter().getOrganizationIds()!=null) {
				criteriaList.add(Criteria.where("receiver.organizationId").in(notificationFilter.getReceiverFilter().getOrganizationIds()));
			}else if(notificationFilter.getReceiverFilter().getOrganizationId()!=null){
				criteriaList.add(Criteria.where("receiver.organizationId").is(notificationFilter.getReceiverFilter().getOrganizationId()));
			}
			
			if(notificationFilter.getReceiverFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("receiver.organizationUserId").is(notificationFilter.getReceiverFilter().getOrganizationUserId()));
			}
		}
		
		if(notificationFilter.getType()!=null) {
			criteriaList.add(Criteria.where("type").is(notificationFilter.getType()));
		}
		
		if(notificationFilter.getAction()!=null) {
			criteriaList.add(Criteria.where("action").is(notificationFilter.getAction()));
		}
		
		if(notificationFilter.getClassId()!=null) {
			criteriaList.add(Criteria.where("classId").is(notificationFilter.getClassId()));
		}
		
		if(notificationFilter.getViewed()!=null) {
			criteriaList.add(Criteria.where("viewed").is(notificationFilter.getViewed().booleanValue()));
		}
		
		if(notificationFilter.getScope()!=null) {
			criteriaList.add(Criteria.where("scope").is(notificationFilter.getScope()));
		}
		
		Query query = new Query();
		if(notificationFilter.getKeySearch()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(notificationFilter.getKeySearch());
			if(notificationFilter.getSearchingTypeFilter()!=null) {
				if(notificationFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(notificationFilter.getKeySearch());
				}else if(notificationFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(notificationFilter.getKeySearch());
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
	public List<Notification> findAll(NotificationFilter notificationFilter) {
		Query query = buildQuery(notificationFilter);
		
		if(notificationFilter!=null && notificationFilter.getSkipLimitFilter()!=null) {
			query.skip(notificationFilter.getSkipLimitFilter().getSkip());
			query.limit(notificationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(notificationFilter!=null && notificationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(notificationFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Notification.class);
	}

	@Override
	public long countAll(NotificationFilter notificationFilter) {
		Query query = buildQuery(notificationFilter);
		return this.mongoTemplate.count(query, Notification.class);
	}

	@Override
	public Optional<Notification> findOne(NotificationFilter notificationFilter) {
		Query query = buildQuery(notificationFilter);
		
		if(notificationFilter!=null && notificationFilter.getSkipLimitFilter()!=null) {
			query.skip(notificationFilter.getSkipLimitFilter().getSkip());
			query.limit(notificationFilter.getSkipLimitFilter().getLimit());
		}
		
		if(notificationFilter!=null && notificationFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(notificationFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Notification.class));
	}

	@Override
	public long setMarkAll(NotificationFilter notificationFilter) {
		Query query = buildQuery(notificationFilter);
		
		Update update=new Update();
		update.set("viewed", true);
		update.set("viewedTime", new Date());
		
		UpdateResult updateResult=mongoTemplate.updateMulti(query, update, Notification.class);
		return updateResult.getModifiedCount();
	}

}
