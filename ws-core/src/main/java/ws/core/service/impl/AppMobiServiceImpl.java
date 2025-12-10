package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;
import ws.core.respository.AppMobiRepository;
import ws.core.respository.AppMobiRepositoryCustom;
import ws.core.services.AppMobiService;

@Service
public class AppMobiServiceImpl implements AppMobiService{

	@Autowired
	private AppMobiRepository appMobiRepository;
	
	@Autowired
	private AppMobiRepositoryCustom appMobiRepositoryCustom;
	
	@Override
	public long countAppMobiAll(AppMobiFilter appMobiFilter) {
		return appMobiRepositoryCustom.countAll(appMobiFilter);
	}

	@Override
	public List<AppMobi> findAppMobiAll(AppMobiFilter appMobiFilter) {
		return appMobiRepositoryCustom.findAll(appMobiFilter);
	}

	@Override
	public AppMobi findAppMobiById(String id) {
		Optional<AppMobi> appMobi = appMobiRepository.findById(new ObjectId(id));
		if(appMobi.isPresent()) {
			return appMobi.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy thiết bị");
	}

	@Override
	public AppMobi deleteAppMobiById(String id) {
		Optional<AppMobi> findAppMobi=appMobiRepository.findById(new ObjectId(id));
		if(findAppMobi.isPresent()) {
			appMobiRepository.delete(findAppMobi.get());
			return findAppMobi.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy thiết bị");
	}

	@Override
	public AppMobi setAppMobiActive(String id, boolean active) {
		Optional<AppMobi> findAppMobi = appMobiRepository.findById(new ObjectId(id));
		if(findAppMobi.isPresent()) {
			AppMobi appMobi=findAppMobi.get();
			appMobi.setActive(active);
			return appMobiRepository.save(appMobi);
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy thiết bị");
	}

	

}
