package ws.core.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.Upgrade;
import ws.core.respository.UpgradeRepository;
import ws.core.services.UpgradeService;

@Service
public class UpgradeServiceImpl implements UpgradeService{
	
	@Autowired
	private UpgradeRepository upgradeRepository;
	
	@Override
	public boolean existsName(String name) {
		Optional<Upgrade> findUpgrade=upgradeRepository.findByName(name);
		return findUpgrade.isPresent();
	}

	@Override
	public Upgrade save(Upgrade upgrade) {
		return upgradeRepository.save(upgrade);
	}

}
