package ws.core.services;

import ws.core.model.Upgrade;

public interface UpgradeService {
	public boolean existsName(String name);
	
	public Upgrade save(Upgrade upgrade);
}
