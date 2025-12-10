package ws.core.services;

import java.util.List;

import ws.core.model.LeaderApproveTask;
import ws.core.model.User;
import ws.core.model.filter.LeaderApproveTaskFilter;
import ws.core.model.request.ReqLeaderApproveTaskCreate;
import ws.core.model.request.ReqLeaderApproveTaskUpdate;

public interface LeaderApproveTaskService {
	public long countLeaderApproveTaskAll(LeaderApproveTaskFilter leaderApproveTaskFilter);
	
	public List<LeaderApproveTask> findLeaderApproveTaskAll(LeaderApproveTaskFilter leaderApproveTaskFilter);
	
	public LeaderApproveTask findLeaderApproveTaskById(String id);
	
	public LeaderApproveTask deleteLeaderApproveTaskById(String id);

	public LeaderApproveTask createLeaderApproveTask(ReqLeaderApproveTaskCreate reqLeaderApproveTaskCreate, User creator);

	public LeaderApproveTask updateLeaderApproveTask(String leaderApproveTaskId, ReqLeaderApproveTaskUpdate reqLeaderApproveTaskUpdate);
}
