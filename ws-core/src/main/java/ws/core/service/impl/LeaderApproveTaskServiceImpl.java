package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.LeaderApproveTask;
import ws.core.model.User;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.LeaderApproveTaskFilter;
import ws.core.model.request.ReqLeaderApproveTaskCreate;
import ws.core.model.request.ReqLeaderApproveTaskUpdate;
import ws.core.respository.LeaderApproveTaskRepository;
import ws.core.respository.LeaderApproveTaskRepositoryCustom;
import ws.core.services.DocService;
import ws.core.services.LeaderApproveTaskService;

@Service
public class LeaderApproveTaskServiceImpl implements LeaderApproveTaskService{

	@Autowired
	private LeaderApproveTaskRepository leaderApproveTaskRepository;
	
	@Autowired
	private LeaderApproveTaskRepositoryCustom leaderApproveTaskRepositoryCustom;
	
	@Autowired
	private DocService docService;
	
	@Override
	public long countLeaderApproveTaskAll(LeaderApproveTaskFilter leaderApproveTaskFilter) {
		return leaderApproveTaskRepositoryCustom.countAll(leaderApproveTaskFilter);
	}

	@Override
	public List<LeaderApproveTask> findLeaderApproveTaskAll(LeaderApproveTaskFilter leaderApproveTaskFilter) {
		return leaderApproveTaskRepositoryCustom.findAll(leaderApproveTaskFilter);
	}

	@Override
	public LeaderApproveTask findLeaderApproveTaskById(String id) {
		Optional<LeaderApproveTask> findLeaderApproveTask=leaderApproveTaskRepository.findById(new ObjectId(id));
		if(findLeaderApproveTask.isPresent()) {
			return findLeaderApproveTask.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy");
	}

	@Override
	public LeaderApproveTask deleteLeaderApproveTaskById(String id) {
		LeaderApproveTask leaderApproveTask=findLeaderApproveTaskById(id);
		
		DocFilter docFilter=new DocFilter();
		docFilter.setLeaderApproveTaskId(leaderApproveTask.getId());
		
		if(docService.countDocAll(docFilter)==0) {
			leaderApproveTaskRepository.delete(leaderApproveTask);
			return leaderApproveTask;
		}
		throw new NotAcceptableExceptionAdvice("Không thể xóa, vì dữ liệu đã được sử dụng");
	}

	@Override
	public LeaderApproveTask createLeaderApproveTask(ReqLeaderApproveTaskCreate reqLeaderApproveTaskCreate, User creator) {
		LeaderApproveTask leaderApproveTask=new LeaderApproveTask();
		leaderApproveTask.setName(reqLeaderApproveTaskCreate.getName());
		leaderApproveTask.setOrder(reqLeaderApproveTaskCreate.getOrder());
		leaderApproveTask.setOrganizationId(reqLeaderApproveTaskCreate.getOrganizationId());
		leaderApproveTask.setOrganizationName(reqLeaderApproveTaskCreate.getOrganizationName());
		leaderApproveTask.setCreator(reqLeaderApproveTaskCreate.getCreator().toCreator());
		leaderApproveTask.setActive(reqLeaderApproveTaskCreate.isActive());
		
		return leaderApproveTaskRepository.save(leaderApproveTask);
	}

	@Override
	public LeaderApproveTask updateLeaderApproveTask(String leaderApproveTaskId, ReqLeaderApproveTaskUpdate reqLeaderApproveTaskUpdate) {
		LeaderApproveTask leaderApproveTask=findLeaderApproveTaskById(leaderApproveTaskId);
		leaderApproveTask.setUpdatedTime(new Date());
		leaderApproveTask.setName(reqLeaderApproveTaskUpdate.getName());
		leaderApproveTask.setOrder(reqLeaderApproveTaskUpdate.getOrder());
		leaderApproveTask.setActive(reqLeaderApproveTaskUpdate.isActive());
		
		return leaderApproveTaskRepository.save(leaderApproveTask);
	}

}
