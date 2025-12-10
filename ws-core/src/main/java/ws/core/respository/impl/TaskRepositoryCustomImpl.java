package ws.core.respository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.enums.TaskCompleteStatus;
import ws.core.enums.TaskState;
import ws.core.model.Task;
import ws.core.model.embeded.TaskDocInfo;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.TaskAssigneeFilter;
import ws.core.model.filter.embeded.TaskAssistantFilter;
import ws.core.model.filter.embeded.TaskFollowerFilter;
import ws.core.model.filter.embeded.TaskOwnerFilter;
import ws.core.model.filter.embeded.TaskSupportFilter;
import ws.core.model.filter.embeded.TaskUserRefFilter;
import ws.core.respository.TaskRepositoryCustom;
import ws.core.services.PropsService;
import ws.core.util.DateTimeUtil;

@Repository
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom{
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	private PropsService propsService;
	
	private List<Criteria> createCriteria(TaskFilter taskFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		if(taskFilter==null)
			return criteriaList;
		
		/* Tìm theo ids */
		if(taskFilter.getIds()!=null) {
			criteriaList.add(Criteria.where("_id").in(taskFilter.getIds()));
		}
		
		if(taskFilter.getKeySearch()!=null) {
			List<Criteria> criterias = new ArrayList<>();
			String [] listKeys= {"title","description"};
			for (String key : listKeys) {
				if(taskFilter.getKeySearch().contains("*")) {
					Criteria findOr = Criteria.where(key).is(taskFilter.getKeySearch());
					criterias.add(findOr);
				}else {
					Criteria findOr = Criteria.where(key).regex(".*"+taskFilter.getKeySearch()+".*", "iu");
					criterias.add(findOr);
				}
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Tìm theo khung thời gian giao */
		if(taskFilter.getFromDate()>0 && taskFilter.getToDate()>0) {
			Date fromDate=new Date(taskFilter.getFromDate());
			Date toDate=new Date(taskFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lt(toDate));
		}else if(taskFilter.getFromDate()>0) {
			Date fromDate=new Date(taskFilter.getFromDate());
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(taskFilter.getToDate()>0) {
			Date toDate=new Date(taskFilter.getToDate());
			criteriaList.add(Criteria.where("createdTime").lt(toDate));
		}
		
		/* Tìm theo khung thời gian hoàn thành */
		if(taskFilter.getCompletedFromDate()>0 && taskFilter.getCompletedToDate()>0) {
			Date fromDate=new Date(taskFilter.getCompletedFromDate());
			Date toDate=new Date(taskFilter.getCompletedToDate());
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").gte(fromDate).lt(toDate));
		}else if(taskFilter.getCompletedFromDate()>0) {
			Date fromDate=new Date(taskFilter.getCompletedFromDate());
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").gte(fromDate));
		}else if(taskFilter.getCompletedToDate()>0) {
			Date toDate=new Date(taskFilter.getCompletedToDate());
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").lte(toDate));
		}
		
		/* Tìm findOwnerFilters */
		if(taskFilter.getFindOwnerFilters()!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			
			for(TaskOwnerFilter owner:taskFilter.getFindOwnerFilters()) {
				/* Mix owner + assistant */
				if(owner.getOnlyOwner()==null) {
					List<Criteria> iTemOwnerCriterias = new ArrayList<>();
					if(owner.getOrganizationIds()!=null) {
						iTemOwnerCriterias.add(Criteria.where("owner.organizationId").in(owner.getOrganizationIds()));
					}else if(owner.getOrganizationId()!=null) {
						iTemOwnerCriterias.add(Criteria.where("owner.organizationId").is(owner.getOrganizationId()));
					}
					
					if(owner.getOrganizationUserId()!=null) {
						iTemOwnerCriterias.add(Criteria.where("owner.organizationUserId").is(owner.getOrganizationUserId()));
					}
					
					List<Criteria> iTemAssistantCriterias = new ArrayList<>();
					if(owner.getOrganizationIds()!=null) {
						iTemAssistantCriterias.add(Criteria.where("assistant.organizationId").in(owner.getOrganizationIds()));
					}else if(owner.getOrganizationId()!=null) {
						iTemAssistantCriterias.add(Criteria.where("assistant.organizationId").is(owner.getOrganizationId()));
					}
					
					if(owner.getOrganizationUserId()!=null) {
						iTemAssistantCriterias.add(Criteria.where("assistant.organizationUserId").is(owner.getOrganizationUserId()));
					}
					
					List<Criteria> orTemMixCriterias = new ArrayList<Criteria>();
					if(iTemOwnerCriterias.size()>0) {
						orTemMixCriterias.add(new Criteria().andOperator(iTemOwnerCriterias.toArray(new Criteria[iTemOwnerCriterias.size()])));
					}
					if(iTemAssistantCriterias.size()>0) {
						orTemMixCriterias.add(new Criteria().andOperator(iTemAssistantCriterias.toArray(new Criteria[iTemAssistantCriterias.size()])));
					}
					
					if(orTemMixCriterias.size()>0) {
						criteriaList.add(new Criteria().orOperator(orTemMixCriterias.toArray(new Criteria[orTemMixCriterias.size()])));
					}
				} 
				/* Only owner */
				else if(owner.getOnlyOwner().booleanValue()==true) {
					List<Criteria> iTemCriterias = new ArrayList<>();
					if(owner.getOrganizationIds()!=null) {
						iTemCriterias.add(Criteria.where("owner.organizationId").in(owner.getOrganizationIds()));
					}else if(owner.getOrganizationId()!=null) {
						iTemCriterias.add(Criteria.where("owner.organizationId").is(owner.getOrganizationId()));
					}
					
					if(owner.getOrganizationUserId()!=null) {
						iTemCriterias.add(Criteria.where("owner.organizationUserId").is(owner.getOrganizationUserId()));
					}
					
					if(iTemCriterias.size()>0) {
						orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
					}
				} 
				/* Only asssistant */
				else if(owner.getOnlyOwner().booleanValue()==false) {
					List<Criteria> iTemCriterias = new ArrayList<>();
					if(owner.getOrganizationIds()!=null) {
						iTemCriterias.add(Criteria.where("assistant.organizationId").in(owner.getOrganizationIds()));
					}else if(owner.getOrganizationId()!=null) {
						iTemCriterias.add(Criteria.where("assistant.organizationId").is(owner.getOrganizationId()));
					}
					
					if(owner.getOrganizationUserId()!=null) {
						iTemCriterias.add(Criteria.where("assistant.organizationUserId").is(owner.getOrganizationUserId()));
					}
					
					if(iTemCriterias.size()>0) {
						orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
					}
				}
				
			}
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findAssistants */
		if(taskFilter.getFindAssistantFilters()!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			for(TaskAssistantFilter assistant:taskFilter.getFindAssistantFilters()) {
				List<Criteria> iTemCriterias = new ArrayList<>();
				if(assistant.getOrganizationIds()!=null) {
					iTemCriterias.add(Criteria.where("assistant.organizationId").in(assistant.getOrganizationIds()));
				}else if(assistant.getOrganizationId()!=null) {
					iTemCriterias.add(Criteria.where("assistant.organizationId").is(assistant.getOrganizationId()));
				}
				
				if(assistant.getOrganizationGroupId()!=null) {
					iTemCriterias.add(Criteria.where("assistant.organizationGroupId").is(assistant.getOrganizationGroupId()));
				}
				
				if(assistant.getOrganizationUserId()!=null) {
					iTemCriterias.add(Criteria.where("assistant.organizationUserId").is(assistant.getOrganizationUserId()));
				}
				
				if(iTemCriterias.size()>0) {
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findAssignees */
		if(taskFilter.getFindAssigneeFilters()!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			for(TaskAssigneeFilter assignee:taskFilter.getFindAssigneeFilters()) {
				List<Criteria> iTemCriterias = new ArrayList<>();
				if(assignee.getOrganizationIds()!=null) {
					iTemCriterias.add(Criteria.where("assignee.organizationId").in(assignee.getOrganizationIds()));
				}else if(assignee.getOrganizationId()!=null) {
					iTemCriterias.add(Criteria.where("assignee.organizationId").is(assignee.getOrganizationId()));
				}
				
				if(assignee.getOrganizationUserId()!=null) {
					iTemCriterias.add(Criteria.where("assignee.organizationUserId").is(assignee.getOrganizationUserId()));
				}else {
					/* Tìm đã phân cán bộ xử lý hay chưa*/
					if(taskFilter.getHasAssignUserAssignee()!=null) {
						criteriaList.add(Criteria.where("assignee.organizationUserId").exists(taskFilter.getHasAssignUserAssignee().booleanValue()));
					}
				}
				
				if(iTemCriterias.size()>0) {
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findSupports */
		if(taskFilter.getFindSupportFilters()!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			for(TaskSupportFilter support:taskFilter.getFindSupportFilters()) {
				List<Criteria> iTemCriterias = new ArrayList<>();
				if(support.getOrganizationIds()!=null) {
					iTemCriterias.add(Criteria.where("organizationId").in(support.getOrganizationIds()));
				}else if(support.getOrganizationId()!=null) {
					iTemCriterias.add(Criteria.where("organizationId").is(support.getOrganizationId()));
				}
				
				if(support.getOrganizationUserId()!=null) {
					iTemCriterias.add(Criteria.where("organizationUserId").is(support.getOrganizationUserId()));
				}else {
					/* Tìm đã phân cán bộ hỗ trợ hay chưa*/
					if(taskFilter.getHasAssignUserSupport()!=null) {
						criteriaList.add(Criteria.where("organizationUserId").exists(taskFilter.getHasAssignUserSupport().booleanValue()));
					}
				}
				
				if(iTemCriterias.size()>0) {
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(Criteria.where("supports").elemMatch(new Criteria().andOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()]))));
			}
		}
		
		/* Tìm findFollower */
		if(taskFilter.getFindFollowerFilters()!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			for(TaskFollowerFilter follower:taskFilter.getFindFollowerFilters()) {
				List<Criteria> iTemCriterias = new ArrayList<>();
				if(follower.getOrganizationIds()!=null) {
					iTemCriterias.add(Criteria.where("organizationId").in(follower.getOrganizationIds()));
				}else if(follower.getOrganizationId()!=null) {
					iTemCriterias.add(Criteria.where("organizationId").is(follower.getOrganizationId()));
				}
				
				if(follower.getOrganizationGroupId()!=null) {
					iTemCriterias.add(Criteria.where("organizationGroupId").is(follower.getOrganizationGroupId()));
				}
				
				if(follower.getOrganizationUserId()!=null) {
					iTemCriterias.add(Criteria.where("organizationUserId").is(follower.getOrganizationUserId()));
				}
				
				if(iTemCriterias.size()>0) {
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			
			if(orTemCriterias.size()>0) {
				criteriaList.add(Criteria.where("followers").elemMatch(new Criteria().andOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()]))));
			}
		}
		
		/* Tìm findUserRef */
		if(taskFilter.getFindUserRefFilter()!=null) {
			TaskUserRefFilter taskUserRefFilter=taskFilter.getFindUserRefFilter();
			
			/* Tìm trong owner */
			List<Criteria> iOwner = new ArrayList<>();
			if(taskUserRefFilter.getOrganizationId()!=null) {
				iOwner.add(Criteria.where("owner.organizationId").is(taskUserRefFilter.getOrganizationId()));
			}
			if(taskUserRefFilter.getOrganizationUserId()!=null) {
				iOwner.add(Criteria.where("owner.organizationUserId").is(taskUserRefFilter.getOrganizationUserId()));
			}
			
			/* Tìm trong assistant */
			List<Criteria> isAssistant = new ArrayList<>();
			if(taskUserRefFilter.getOrganizationId()!=null) {
				isAssistant.add(Criteria.where("assistant.organizationId").is(taskUserRefFilter.getOrganizationId()));
			}
			if(taskUserRefFilter.getOrganizationUserId()!=null) {
				isAssistant.add(Criteria.where("assistant.organizationUserId").is(taskUserRefFilter.getOrganizationUserId()));
			}
			
			/* Tìm trong assignee */
			List<Criteria> isAssignee = new ArrayList<>();
			if(taskUserRefFilter.getOrganizationId()!=null) {
				isAssignee.add(Criteria.where("assignee.organizationId").is(taskUserRefFilter.getOrganizationId()));
			}
			if(taskUserRefFilter.getOrganizationUserId()!=null) {
				isAssignee.add(Criteria.where("assignee.organizationUserId").is(taskUserRefFilter.getOrganizationUserId()));
			}
			
			/* Tìm trong supports */
			List<Criteria> isSupports = new ArrayList<>();
			if(taskUserRefFilter.getOrganizationId()!=null) {
				isSupports.add(Criteria.where("organizationId").is(taskUserRefFilter.getOrganizationId()));
			}
			if(taskUserRefFilter.getOrganizationUserId()!=null) {
				isSupports.add(Criteria.where("organizationUserId").is(taskUserRefFilter.getOrganizationUserId()));
			}
			
			/* Tìm trong followers */
			List<Criteria> isFollowers = new ArrayList<>();
			if(taskUserRefFilter.getOrganizationId()!=null) {
				isFollowers.add(Criteria.where("organizationId").is(taskUserRefFilter.getOrganizationId()));
			}
			if(taskUserRefFilter.getOrganizationUserId()!=null) {
				isFollowers.add(Criteria.where("organizationUserId").is(taskUserRefFilter.getOrganizationUserId()));
			}
			
			List<Criteria> orTemCriterias = new ArrayList<>();
			if(iOwner.size()>0) {
				orTemCriterias.add(new Criteria().andOperator(iOwner.toArray(new Criteria[iOwner.size()])));
			}
			if(isAssistant.size()>0) {
				orTemCriterias.add(new Criteria().andOperator(isAssistant.toArray(new Criteria[isAssistant.size()])));
			}
			if(isAssignee.size()>0) {
				orTemCriterias.add(new Criteria().andOperator(isAssignee.toArray(new Criteria[isAssignee.size()])));
			}
			if(isSupports.size()>0) {
				orTemCriterias.add(Criteria.where("supports").elemMatch(new Criteria().andOperator(isSupports.toArray(new Criteria[isSupports.size()]))));
			}
			if(isFollowers.size()>0) {
				orTemCriterias.add(Criteria.where("followers").elemMatch(new Criteria().andOperator(isFollowers.toArray(new Criteria[isFollowers.size()]))));
			}
			
			criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
		}
		
		
		/* Tìm theo status */
		if(taskFilter.getStatus()!=null) {
			switch (taskFilter.getStatus()) {
				case tuchoithuchien: {
					criteriaList.add(Criteria.where("state").is(TaskState.tuchoithuchien.getKey()));
					break;
				}
				case chuathuchien: {
					criteriaList.add(Criteria.where("state").is(TaskState.chuathuchien.getKey()));
					break;
				}
				case chuathuchien_sapquahan: {
					criteriaList.add(Criteria.where("state").is(TaskState.chuathuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(true));
					Date from=new Date();
					Date to=DateTimeUtil.nextDate(from, propsService.getTaskFieldEndtimeAboutToExpireDays());
					criteriaList.add(Criteria.where("endTime").gte(from));
					criteriaList.add(Criteria.where("endTime").lt(to));
					break;
				}
				case chuathuchien_quahan:{
					criteriaList.add(Criteria.where("state").is(TaskState.chuathuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(true));
					criteriaList.add(Criteria.where("endTime").lt(new Date()));
					break;
				}
				case thuchienlai: {
					criteriaList.add(Criteria.where("state").is(TaskState.thuchienlai.getKey()));
					break;
				}
				case tamhoan: {
					criteriaList.add(Criteria.where("state").is(TaskState.tamhoan.getKey()));
					break;
				}
				case dangthuchien: {
					criteriaList.add(Criteria.where("state").is(TaskState.dangthuchien.getKey()));
					break;
				}
				case dangthuchien_tronghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dangthuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(true));
					criteriaList.add(Criteria.where("endTime").gte(new Date()));
					break;
				}
				case dangthuchien_sapquahan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dangthuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(true));
					Date from=new Date();
					Date to=DateTimeUtil.nextDate(from, propsService.getTaskFieldEndtimeAboutToExpireDays());
					criteriaList.add(Criteria.where("endTime").gte(from));
					criteriaList.add(Criteria.where("endTime").lt(to));
					break;
				}
				case dangthuchien_quahan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dangthuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(true));
					criteriaList.add(Criteria.where("endTime").lt(new Date()));
					break;
				}
				case dangthuchien_khonghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dangthuchien.getKey()));
					criteriaList.add(Criteria.where("endTime").exists(false));
					break;
				}
				case choxacnhan: {
					criteriaList.add(Criteria.where("state").is(TaskState.choxacnhan.getKey()));
					criteriaList.add(Criteria.where("requiredConfirm").is(true));
					criteriaList.add(Criteria.where("reported").exists(true));
					break;
				}
				case choxacnhan_tronghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.choxacnhan.getKey()));
					criteriaList.add(Criteria.where("requiredConfirm").is(true));
					criteriaList.add(Criteria.where("reported").exists(true));
					criteriaList.add(Criteria.where("reported.reportedStatus").is(TaskCompleteStatus.tronghan.getKey()));
					break;
				}
				case choxacnhan_quahan: {
					criteriaList.add(Criteria.where("state").is(TaskState.choxacnhan.getKey()));
					criteriaList.add(Criteria.where("requiredConfirm").is(true));
					criteriaList.add(Criteria.where("reported").exists(true));
					criteriaList.add(Criteria.where("reported.reportedStatus").is(TaskCompleteStatus.quahan.getKey()));
					break;
				}
				case choxacnhan_khonghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.choxacnhan.getKey()));
					criteriaList.add(Criteria.where("requiredConfirm").is(true));
					criteriaList.add(Criteria.where("reported").exists(true));
					criteriaList.add(Criteria.where("reported.reportedStatus").is(TaskCompleteStatus.khonghan.getKey()));
					break;
				}
				case tuchoixacnhan: {
					criteriaList.add(Criteria.where("state").is(TaskState.tuchoixacnhan.getKey()));
					break;
				}
				case dahoanthanh: {
					criteriaList.add(Criteria.where("state").is(TaskState.dahoanthanh.getKey()));
					criteriaList.add(Criteria.where("completed").exists(true));
					break;
				}
				case dahoanthanh_tronghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dahoanthanh.getKey()));
					criteriaList.add(Criteria.where("completed").exists(true));
					criteriaList.add(Criteria.where("completed.completedStatus").is(TaskCompleteStatus.tronghan.getKey()));
					break;
				}
				case dahoanthanh_quahan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dahoanthanh.getKey()));
					criteriaList.add(Criteria.where("completed").exists(true));
					criteriaList.add(Criteria.where("completed.completedStatus").is(TaskCompleteStatus.quahan.getKey()));
					break;
				}
				case dahoanthanh_khonghan: {
					criteriaList.add(Criteria.where("state").is(TaskState.dahoanthanh.getKey()));
					criteriaList.add(Criteria.where("completed").exists(true));
					criteriaList.add(Criteria.where("completed.completedStatus").is(TaskCompleteStatus.khonghan.getKey()));
					break;
				}
			case has_endtime: {
				criteriaList.add(Criteria.where("endTime").exists(true));
				break;
			}
			case not_endtime: {
				criteriaList.add(Criteria.where("endTime").exists(false));
				break;
			}
			case has_rating: {
				criteriaList.add(Criteria.where("rating").exists(true));
				break;
			}
			case not_rating:{
				criteriaList.add(Criteria.where("rating").exists(false));
				break;
			}
			case wait_rating:{
				criteriaList.add(Criteria.where("rating").exists(false));
				criteriaList.add(Criteria.where("state").is(TaskState.dahoanthanh.getKey()));
				criteriaList.add(Criteria.where("completed").exists(true));
				break;
			}
			case has_required_confirm:{
				criteriaList.add(Criteria.where("requiredConfirm").is(true));
				break;
			}
			case not_required_confirm:{
				criteriaList.add(Criteria.where("requiredConfirm").is(false));
				break;
			}
			case has_remind:{
				criteriaList.add(Criteria.where("reminds").exists(true).ne(Collections.emptyList()));
				break;
			}
			case not_remind:{
				criteriaList.add(Criteria.where("reminds").is(Collections.emptyList()));
				break;
			}
			case has_comment:{
				criteriaList.add(Criteria.where("comments").exists(true).ne(Collections.emptyList()));
				break;
			}
			case not_comment:{
				criteriaList.add(Criteria.where("comments").is(Collections.emptyList()));
				break;
			}
			case has_process:{
				criteriaList.add(Criteria.where("processes").exists(true).ne(Collections.emptyList()));
				break;
			}
			case not_process:{
				criteriaList.add(Criteria.where("processes").is(Collections.emptyList()));
				break;
			}
			case has_subtask:{
				criteriaList.add(Criteria.where("countSubTask").gt(0));
				break;
			}
			case not_subtask:{
				criteriaList.add(Criteria.where("countSubTask").is(0));
				break;
			}
			default:
				break;
			}
		}
		
		/* Tìm theo độ khẩn */
		if(taskFilter.getPriority()!=null) {
			criteriaList.add(Criteria.where("priority").is(taskFilter.getPriority()));
		}
		
		/* Tìm theo parentId */
		if(taskFilter.getParentIds()!=null && taskFilter.getParentIds().size()>0) {
			criteriaList.add(Criteria.where("parentId").in(taskFilter.getParentIds()));
		}
		
		/* Tìm theo docId */
		if(taskFilter.getDocIds()!=null && taskFilter.getDocIds().size()>0) {
			criteriaList.add(Criteria.where("docId").in(taskFilter.getDocIds()));
		}
		
		/* Tìm theo taskRoot */
		if(taskFilter.getTaskRoot()!=null) {
			criteriaList.add(Criteria.where("parentId").exists(!taskFilter.getTaskRoot().booleanValue()));
		}
		
		/* Tìm theo ratingStart */
		if(taskFilter.getRatingStar()!=null) {
			criteriaList.add(Criteria.where("rating.star").in(taskFilter.getRatingStar()));
		}
		
		/* Tìm taskDocInfo */
		if(taskFilter.getDocInfo()!=null) {
			TaskDocInfo taskDocInfo=taskFilter.getDocInfo();
			if(taskDocInfo.getNumber()!=null) {
				criteriaList.add(Criteria.where("docInfo.number").is(taskDocInfo.getNumber()));
			}
			
			if(taskDocInfo.getSymbol()!=null) {
				criteriaList.add(Criteria.where("docInfo.symbol").is(taskDocInfo.getSymbol()));
			}
		}
		
		/* Tìm theo taskSource */
		if(taskFilter.getTaskSource()!=null) {
			switch (taskFilter.getTaskSource()) {
				case fromdoc: {
					criteriaList.add(Criteria.where("docId").exists(true));
					break;
				}
				case personal: {
					criteriaList.add(Criteria.where("docId").exists(false));
					break;
				}
			}
		}
		
		/* Tìm theo Kpi*/
		if(taskFilter.getKpi() != null) {
			criteriaList.add(Criteria.where("requiredKpi").in(taskFilter.getKpi()));
		}
		
		
//		for (Criteria criteria : criteriaList) {
//			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
//		}
		
		return criteriaList;
	}
	
	@Override
	public List<Task> findAll(TaskFilter taskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(taskFilter!=null && taskFilter.getSkipLimitFilter()!=null) {
			query.skip(taskFilter.getSkipLimitFilter().getSkip());
			query.limit(taskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(taskFilter!=null && taskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(taskFilter.getOrderByFilter().getSortBy()));
		}
		
		return this.mongoTemplate.find(query, Task.class);
	}

	@Override
	public long countAll(TaskFilter taskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, Task.class);
	}

	@Override
	public Optional<Task> findOne(TaskFilter taskFilter) {
		Query query = new Query();
		List<Criteria> criteriaList = createCriteria(taskFilter);
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(taskFilter!=null && taskFilter.getSkipLimitFilter()!=null) {
			query.skip(taskFilter.getSkipLimitFilter().getSkip());
			query.limit(taskFilter.getSkipLimitFilter().getLimit());
		}
		
		if(taskFilter!=null && taskFilter.getOrderByFilter()!=null) {
			query.with(Sort.by(taskFilter.getOrderByFilter().getSortBy()));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Task.class));
	}

}
