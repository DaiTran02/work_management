package ws.core.services;

import java.util.List;

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;

public interface AppMobiService {
	public long countAppMobiAll(AppMobiFilter appMobiFilter);
	
	public List<AppMobi> findAppMobiAll(AppMobiFilter appMobiFilter);
	
	public AppMobi findAppMobiById(String id);
	
	public AppMobi deleteAppMobiById(String id);
	
	public AppMobi setAppMobiActive(String id, boolean active);
}
