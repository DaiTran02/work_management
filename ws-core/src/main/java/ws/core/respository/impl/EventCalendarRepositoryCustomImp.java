package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.Date;
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

import ws.core.enums.EventCalendarType;
import ws.core.enums.EventCalendarUserStatus;
import ws.core.model.EventCalendar;
import ws.core.model.filter.EventCalendarFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.respository.EventCalendarRepositoryCustom;

@Repository
public class EventCalendarRepositoryCustomImp implements EventCalendarRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private Query createCriteria(EventCalendarFilter eventCalendarFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		if(eventCalendarFilter.getId()!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(eventCalendarFilter.getId())));
		}
		
		if(eventCalendarFilter.getCreatedFrom()>0 && eventCalendarFilter.getCreatedTo()>0) {
			Date fromDate=new Date(eventCalendarFilter.getCreatedFrom());
			Date toDate=new Date(eventCalendarFilter.getCreatedTo());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(eventCalendarFilter.getCreatedFrom()>0) {
			Date fromDate=new Date(eventCalendarFilter.getCreatedFrom());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(eventCalendarFilter.getCreatedTo()>0) {
			Date toDate=new Date(eventCalendarFilter.getCreatedTo());
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		/* Cách tính from - to cũ */
//		if(eventCalendarFilter.getFromDate()>0 && eventCalendarFilter.getToDate()>0) {
//			Date fromDate=new Date(eventCalendarFilter.getFromDate());
//			Date toDate=new Date(eventCalendarFilter.getToDate());
//			criteriaList.add(Criteria.where("from").gte(fromDate).lte(toDate));
//		}else if(eventCalendarFilter.getFromDate()>0) {
//			Date fromDate=new Date(eventCalendarFilter.getFromDate());
//			criteriaList.add(Criteria.where("from").gte(fromDate));
//		}else if(eventCalendarFilter.getToDate()>0) {
//			Date toDate=new Date(eventCalendarFilter.getToDate());
//			criteriaList.add(Criteria.where("from").lte(toDate));
//		}
		
		/* Cách tính from - to mới */
		if(eventCalendarFilter.getFromDate()>0 && eventCalendarFilter.getToDate()>0) {
			Date toZero=new Date(0);
			Date startDate=new Date(eventCalendarFilter.getFromDate());
			Date endDate=new Date(eventCalendarFilter.getToDate());
			
			/* Trường hợp 1: to==0 AND start <= from <= end */
			List<Criteria> TH1 = new ArrayList<>();
			TH1.add(Criteria.where("to").is(toZero));			
			TH1.add(Criteria.where("from").gte(startDate).lte(endDate));
			
			/* Trường hợp 2: to!=0 AND start <= from <= end AND start <= to <= end */
			List<Criteria> TH2 = new ArrayList<>();
			TH2.add(Criteria.where("to").ne(toZero));			
			TH2.add(Criteria.where("from").gte(startDate).lte(endDate));
			TH2.add(Criteria.where("to").gte(startDate).lte(endDate));
			
			/* Trường hợp 3: to!=0 AND start <= from <= end AND to >= end */
			List<Criteria> TH3 = new ArrayList<>();
			TH3.add(Criteria.where("to").ne(toZero));			
			TH3.add(Criteria.where("from").gte(startDate).lte(endDate));
			TH3.add(Criteria.where("to").gte(endDate));
			
			/* Trường hợp 4: to!=0 AND from <= start AND start <= to <= end */
			List<Criteria> TH4 = new ArrayList<>();
			TH4.add(Criteria.where("to").ne(toZero));			
			TH4.add(Criteria.where("from").lte(startDate));
			TH4.add(Criteria.where("to").gte(startDate).lte(endDate));
			
			/* Trường hợp 5: to!=0 AND from <= start AND to >= end */
			List<Criteria> TH5 = new ArrayList<>();
			TH5.add(Criteria.where("to").ne(toZero));			
			TH5.add(Criteria.where("from").lte(startDate));
			TH5.add(Criteria.where("to").gte(endDate));
			
			
			List<Criteria> orTemCriterias = new ArrayList<>();
			orTemCriterias.add(new Criteria().andOperator(TH1.toArray(new Criteria[TH1.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH2.toArray(new Criteria[TH2.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH3.toArray(new Criteria[TH3.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH4.toArray(new Criteria[TH4.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH5.toArray(new Criteria[TH5.size()])));
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}else if(eventCalendarFilter.getFromDate()>0) {
			Date toZero=new Date(0);
			Date startDate=new Date(eventCalendarFilter.getFromDate());
			
			/* Trường hợp 1: to==0 AND from >= start */
			List<Criteria> TH1 = new ArrayList<>();
			TH1.add(Criteria.where("to").is(toZero));			
			TH1.add(Criteria.where("from").gte(startDate));
			
			/* Trường hợp 2: to!=0 AND to >= start */
			List<Criteria> TH2 = new ArrayList<>();
			TH2.add(Criteria.where("to").ne(toZero));			
			TH2.add(Criteria.where("to").gte(startDate));
			
			List<Criteria> orTemCriterias = new ArrayList<>();
			orTemCriterias.add(new Criteria().andOperator(TH1.toArray(new Criteria[TH1.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH2.toArray(new Criteria[TH2.size()])));
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}else if(eventCalendarFilter.getToDate()>0) {
			Date toZero=new Date(0);
			Date endDate=new Date(eventCalendarFilter.getToDate());
			
			/* Trường hợp 1: to==0 AND from >= start */
			List<Criteria> TH1 = new ArrayList<>();
			TH1.add(Criteria.where("to").is(toZero));			
			TH1.add(Criteria.where("from").lte(endDate));
			
			/* Trường hợp 2: to!=0 AND to >= start */
			List<Criteria> TH2 = new ArrayList<>();
			TH2.add(Criteria.where("to").ne(toZero));			
			TH2.add(Criteria.where("from").lte(endDate));
			
			List<Criteria> orTemCriterias = new ArrayList<>();
			orTemCriterias.add(new Criteria().andOperator(TH1.toArray(new Criteria[TH1.size()])));
			orTemCriterias.add(new Criteria().andOperator(TH2.toArray(new Criteria[TH2.size()])));
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		if(eventCalendarFilter.getTrash()!=null) {
			criteriaList.add(Criteria.where("trash").is(eventCalendarFilter.getTrash().booleanValue()));
		}
		
		/* Tìm theo loại đơn vị hay cá nhân */
		if(eventCalendarFilter.getType()!=null) {
			/* Tìm theo loại Đơn vị */
			if(eventCalendarFilter.getType().equalsIgnoreCase(EventCalendarType.organization.getKey())) {
				criteriaList.add(Criteria.where("type").is(eventCalendarFilter.getType()));
				criteriaList.add(Criteria.where("creator.organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
			}
			/* Tìm theo loại Cá nhân */
			else if(eventCalendarFilter.getType().equalsIgnoreCase(EventCalendarType.personal.getKey())) {
				/* Cá nhân tạo (giùm hoặc cho đơn vị)*/
				List<Criteria> personalCriterias = new ArrayList<>();
				personalCriterias.add(Criteria.where("type").is(eventCalendarFilter.getType()));
				if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
					personalCriterias.add(Criteria.where("creator.organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
				}
				personalCriterias.add(Criteria.where("creator.organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
				
				/* Cá nhân tham gia các event nằm trong hosts, attendeesRequired, attendeesNoRequired */
				List<Criteria> organizationCriterias = new ArrayList<>();
					
				/* Nếu status là delegacy (ủy quyền) thì phải tìm trong cấp trong historiesDelegacy */
				if(eventCalendarFilter.getStatus()!=null && eventCalendarFilter.getStatus().equals(EventCalendarUserStatus.delegacy.getKey())) {
					List<Criteria> hostsCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						hostsCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					hostsCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					//if(eventCalendarFilter.getStatus()!=null) {
					//	hostsCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					//}

					List<Criteria> attendeesRequiredCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						attendeesRequiredCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					attendeesRequiredCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					//if(eventCalendarFilter.getStatus()!=null) {
					//	attendeesRequiredCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					//}

					List<Criteria> attendeesNoRequiredCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						attendeesNoRequiredCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					attendeesNoRequiredCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					//if(eventCalendarFilter.getStatus()!=null) {
					//	attendeesNoRequiredCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					//}

					List<Criteria> prepareresCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						prepareresCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					prepareresCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					
					organizationCriterias.add(Criteria.where("hosts").elemMatch(Criteria.where("historiesDelegacy").elemMatch(new Criteria().andOperator(hostsCriterias.toArray(new Criteria[hostsCriterias.size()])))));
					organizationCriterias.add(Criteria.where("attendeesRequired").elemMatch(Criteria.where("historiesDelegacy").elemMatch(new Criteria().andOperator(attendeesRequiredCriterias.toArray(new Criteria[attendeesRequiredCriterias.size()])))));
					organizationCriterias.add(Criteria.where("attendeesNoRequired").elemMatch(Criteria.where("historiesDelegacy").elemMatch(new Criteria().andOperator(attendeesNoRequiredCriterias.toArray(new Criteria[attendeesNoRequiredCriterias.size()])))));
					organizationCriterias.add(Criteria.where("prepareres").elemMatch(Criteria.where("historiesDelegacy").elemMatch(new Criteria().andOperator(prepareresCriterias.toArray(new Criteria[prepareresCriterias.size()])))));
					
				} 
				/* Bình thường cấp trực tiếp */
				else {
					List<Criteria> hostsCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						hostsCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					hostsCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					if(eventCalendarFilter.getStatus()!=null) {
						hostsCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					}

					List<Criteria> attendeesRequiredCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						attendeesRequiredCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					attendeesRequiredCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					if(eventCalendarFilter.getStatus()!=null) {
						attendeesRequiredCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					}

					List<Criteria> attendeesNoRequiredCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						attendeesNoRequiredCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					attendeesNoRequiredCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					if(eventCalendarFilter.getStatus()!=null) {
						attendeesNoRequiredCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					}

					List<Criteria> prepareresCriterias = new ArrayList<>();
					if(eventCalendarFilter.getCreatorFilter().getOrganizationId()!=null) {
						prepareresCriterias.add(Criteria.where("organizationId").is(eventCalendarFilter.getCreatorFilter().getOrganizationId()));
					}
					prepareresCriterias.add(Criteria.where("organizationUserId").is(eventCalendarFilter.getCreatorFilter().getOrganizationUserId()));
					if(eventCalendarFilter.getStatus()!=null) {
						prepareresCriterias.add(Criteria.where("status").is(eventCalendarFilter.getStatus()));
					}
					
					organizationCriterias.add(Criteria.where("hosts").elemMatch(new Criteria().andOperator(hostsCriterias.toArray(new Criteria[hostsCriterias.size()]))));
					organizationCriterias.add(Criteria.where("attendeesRequired").elemMatch(new Criteria().andOperator(attendeesRequiredCriterias.toArray(new Criteria[attendeesRequiredCriterias.size()]))));
					organizationCriterias.add(Criteria.where("attendeesNoRequired").elemMatch(new Criteria().andOperator(attendeesNoRequiredCriterias.toArray(new Criteria[attendeesNoRequiredCriterias.size()]))));
					organizationCriterias.add(Criteria.where("prepareres").elemMatch(new Criteria().andOperator(prepareresCriterias.toArray(new Criteria[prepareresCriterias.size()]))));
				}
				
				/*
				 * Điều kiện tìm kiếm hoặc giữa hosts, attandeesRequired và attandeesNoRequired
				 */
				List<Criteria> orTemCriterias = new ArrayList<>();
				if(organizationCriterias.size()>0) {
					orTemCriterias.add(new Criteria().orOperator(organizationCriterias.toArray(new Criteria[organizationCriterias.size()])));
				}
				
				/* Kiểm tra trường hợp muốn loại những event chính tôi là người soạn */
				if(eventCalendarFilter.getExcludeCreator()!=null && eventCalendarFilter.getExcludeCreator().booleanValue()) {
					
				} else {
					orTemCriterias.add(new Criteria().andOperator(personalCriterias.toArray(new Criteria[personalCriterias.size()])));
				}
				
				/* Kiểm tra nếu có điều kiện thì thêm vào tìm kiếm */
				if(orTemCriterias.size()>0) {
					criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
				}
			}
		}
		
		if(eventCalendarFilter.getBeforeEventTime()>0) {
			criteriaList.add(Criteria.where("notifyBeforeEvent").is(false));
			criteriaList.add(Criteria.where("from").exists(true));
			
			Date from=new Date();
			Date to=new Date(eventCalendarFilter.getBeforeEventTime());
			criteriaList.add(Criteria.where("from").gte(from).lte(to));
		}
		
		Query query = new Query();
		if(eventCalendarFilter.getKeyword()!=null) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(eventCalendarFilter.getKeyword());
			if(eventCalendarFilter.getSearchingTypeFilter()!=null) {
				if(eventCalendarFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matching)) {
					criteria = TextCriteria.forDefaultLanguage().matching(eventCalendarFilter.getKeyword());
				}else if(eventCalendarFilter.getSearchingTypeFilter().equals(SearchingTypeFilter.matchingAny)) {
					criteria = TextCriteria.forDefaultLanguage().matchingAny(eventCalendarFilter.getKeyword());
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
	public List<EventCalendar> findAll(EventCalendarFilter eventCalendarFilter) {
		Query query = createCriteria(eventCalendarFilter);

		if(eventCalendarFilter!=null && eventCalendarFilter.getSkipLimitFilter()!=null) {
			query.skip(eventCalendarFilter.getSkipLimitFilter().getSkip());
			query.limit(eventCalendarFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventCalendarFilter!=null && eventCalendarFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventCalendarFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, EventCalendar.class);
	}

	@Override
	public long countAll(EventCalendarFilter eventCalendarFilter) {
		Query query = createCriteria(eventCalendarFilter);
		return (int) this.mongoTemplate.count(query, EventCalendar.class);
	}

	@Override
	public Optional<EventCalendar> findOne(EventCalendarFilter eventCalendarFilter) {
		Query query = createCriteria(eventCalendarFilter);
		
		if(eventCalendarFilter!=null && eventCalendarFilter.getSkipLimitFilter()!=null) {
			query.skip(eventCalendarFilter.getSkipLimitFilter().getSkip());
			query.limit(eventCalendarFilter.getSkipLimitFilter().getLimit());
		}
		
		if(eventCalendarFilter!=null && eventCalendarFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(eventCalendarFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, EventCalendar.class));
	}
	
}
