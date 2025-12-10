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
import org.springframework.stereotype.Repository;

import ws.core.enums.DocStatus;
import ws.core.model.Doc;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.embeded.DocUserRefFilter;
import ws.core.respository.DocRepositoryCustom;

@Repository
public class DocRepositoryCustomImp implements DocRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(DocFilter docFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		if(docFilter==null)
			return criteriaList;
		
		/* Tìm theo Ids*/
		if(docFilter.getIds() != null) {
			criteriaList.add(Criteria.where("_id").is(docFilter.getIds()));
		}
		
		/* Tìm theo khung thời gian ngày ký văn bản*/
		if(docFilter.getFromRegDate()>0 && docFilter.getToRegDate()>0) {
			Date fromDate=new Date(docFilter.getFromRegDate());
			Date toDate=new Date(docFilter.getToRegDate());
			criteriaList.add(Criteria.where("regDate").gte(fromDate).lte(toDate));
		}else if(docFilter.getFromRegDate()>0) {
			Date fromDate=new Date(docFilter.getFromRegDate());
			criteriaList.add(Criteria.where("regDate").gte(fromDate));
		}else if(docFilter.getToRegDate()>0) {
			Date toDate=new Date(docFilter.getToRegDate());
			criteriaList.add(Criteria.where("regDate").lte(toDate));
		}
		
		if(docFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>(); 
			String [] listKeys= {"security","number","symbol","type","signerName","summary"};
			for (String key : listKeys) {
				if(docFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(docFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+docFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Tìm Đơn vị/Cán bộ quản lý văn bản */
		if(docFilter.getOwnerFilter()!=null) {
			if(docFilter.getOwnerFilter().getOrganizationIds()!=null) {
				criteriaList.add(Criteria.where("owner.organizationId").in(docFilter.getOwnerFilter().getOrganizationIds()));
			}else if(docFilter.getOwnerFilter().getOrganizationId()!=null){
				criteriaList.add(Criteria.where("owner.organizationId").is(docFilter.getOwnerFilter().getOrganizationId()));
			}
			
			if(docFilter.getOwnerFilter().getOrganizationGroupId()!=null) {
				criteriaList.add(Criteria.where("owner.organizationGroupId").is(docFilter.getOwnerFilter().getOrganizationGroupId()));
			}
			
			if(docFilter.getOwnerFilter().getOrganizationUserId()!=null) {
				criteriaList.add(Criteria.where("owner.organizationUserId").is(docFilter.getOwnerFilter().getOrganizationUserId()));
			}
		}
		
		/* Tìm Đơn vị/Cán bộ có liên quan bất kỳ văn bản nào */
		if(docFilter.getUserRefFilter()!=null) {
			DocUserRefFilter docUserRefFilter=docFilter.getUserRefFilter();
			
			/* Tìm owner */
			List<Criteria> iOwner = new ArrayList<>();
			if(docUserRefFilter.getOrganizationId()!=null){
				iOwner.add(Criteria.where("owner.organizationId").is(docUserRefFilter.getOrganizationId()));
			}
			if(docUserRefFilter.getOrganizationUserId()!=null) {
				iOwner.add(Criteria.where("owner.organizationUserId").is(docUserRefFilter.getOrganizationUserId()));
			}
			
			/* ..... Tìm thêm các đối tượng khác ở đây */
			
			
			List<Criteria> orTemCriterias = new ArrayList<>();
			if(iOwner.size()>0) {
				orTemCriterias.add(new Criteria().andOperator(iOwner.toArray(new Criteria[iOwner.size()])));
			}
			
			criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
		}
		
		if(docFilter.getNumber()!=null) {
			criteriaList.add(Criteria.where("number").is(docFilter.getNumber()));
		}
		
		if(docFilter.getSymbol()!=null) {
			criteriaList.add(Criteria.where("symbol").is(docFilter.getSymbol()));
		}
		
		if(docFilter.getSignerName()!=null) {
			criteriaList.add(Criteria.where("signerName").is(docFilter.getSignerName()));
		}
		
		if(docFilter.getCategory()!=null) {
			criteriaList.add(Criteria.where("category").is(docFilter.getCategory().getKey()));
		}
		
		if(docFilter.getActive()!=null) {
			criteriaList.add(Criteria.where("active").is(docFilter.getActive()));
		}
		
		if(docFilter.getStatus()!=null) {
			switch (docFilter.getStatus()) {
				case dangthuchien:
					criteriaList.add(Criteria.where("countTask").gt(0));
					criteriaList.add(Criteria.where("status").ne(DocStatus.vanbandahoanthanh));
				break;
				case chuagiaonhiemvu:
					criteriaList.add(Criteria.where("countTask").lte(0));
					criteriaList.add(Criteria.where("status").is(DocStatus.chuagiaonhiemvu));
				break;
				case vanbandahoanthanh:
					criteriaList.add(Criteria.where("status").is(DocStatus.vanbandahoanthanh));
				break;
			}
		}
		
		if(docFilter.getClassifyTaskId()!=null) {
			criteriaList.add(Criteria.where("classifyTaskId").is(docFilter.getClassifyTaskId()));
		}
		
		if(docFilter.getLeaderApproveTaskId()!=null) {
			criteriaList.add(Criteria.where("leaderApproveTaskId").is(docFilter.getLeaderApproveTaskId()));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<Doc> findAll(DocFilter docFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(docFilter!=null && docFilter.getSkipLimitFilter()!=null) {
			query.skip(docFilter.getSkipLimitFilter().getSkip());
			query.limit(docFilter.getSkipLimitFilter().getLimit());
		}
		
		if(docFilter!=null && docFilter.getOrderByFilter()!=null) {
			System.out.println("Check filter: "+docFilter.getOrderByFilter().getSortBy());
			query.with(Sort.by(docFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Doc.class);
	}

	@Override
	public long countAll(DocFilter docFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, Doc.class);
	}

	@Override
	public Optional<Doc> findOne(DocFilter docFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(docFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(docFilter!=null && docFilter.getSkipLimitFilter()!=null) {
			query.skip(docFilter.getSkipLimitFilter().getSkip());
			query.limit(docFilter.getSkipLimitFilter().getLimit());
		}
		
		if(docFilter!=null && docFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(docFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Doc.class));
	}

}
