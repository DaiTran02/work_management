package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.LeaderApproveTask;
import ws.core.model.filter.LeaderApproveTaskFilter;

public interface LeaderApproveTaskRepositoryCustom {
	List<LeaderApproveTask> findAll(LeaderApproveTaskFilter leaderApproveTaskFilter);
	long countAll(LeaderApproveTaskFilter leaderApproveTaskFilter);
	Optional<LeaderApproveTask> findOne(LeaderApproveTaskFilter leaderApproveTaskFilter);
}
