package ws.core.services;

import java.util.List;

import ws.core.model.AppAccess;
import ws.core.model.User;
import ws.core.model.filter.AppAccessFilter;
import ws.core.model.request.ReqAppAccessCreate;
import ws.core.model.request.ReqAppAccessUpdate;

public interface AppAccessService {
	public long countAppAccessAll(AppAccessFilter appAccessFilter);
	
	public List<AppAccess> findAppAccessAll(AppAccessFilter appAccessFilter);
	
	public AppAccess findAppAccessById(String id);
	
	public AppAccess findAppAccessByApiKey(String apiKey);
	
	public AppAccess deleteAppAccessById(String id);
	
	public AppAccess createAppAccess(ReqAppAccessCreate reqAppAccessCreate, User creator);
	
	public AppAccess updateAppAccess(String appAccessId, ReqAppAccessUpdate reqAppAccessUpdate);
}
