package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.AppAccess;
import ws.core.model.User;
import ws.core.model.filter.AppAccessFilter;
import ws.core.model.request.ReqAppAccessCreate;
import ws.core.model.request.ReqAppAccessUpdate;
import ws.core.respository.AppAccessRepository;
import ws.core.respository.AppAccessRepositoryCustom;
import ws.core.services.AppAccessService;

@Service
public class AppAccessServiceImpl implements AppAccessService{

	@Autowired
	private AppAccessRepository appAccessRepository;
	
	@Autowired
	private AppAccessRepositoryCustom appAccessRepositoryCustom;
	
	@Override
	public long countAppAccessAll(AppAccessFilter appAccessFilter) {
		return appAccessRepositoryCustom.countAll(appAccessFilter);
	}

	@Override
	public List<AppAccess> findAppAccessAll(AppAccessFilter appAccessFilter) {
		return appAccessRepositoryCustom.findAll(appAccessFilter);
	}

	@Override
	public AppAccess findAppAccessById(String id) {
		Optional<AppAccess> appMobi = appAccessRepository.findById(new ObjectId(id));
		if(appMobi.isPresent()) {
			return appMobi.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy kết quả");
	}

	@Override
	public AppAccess deleteAppAccessById(String id) {
		AppAccess appAccess=findAppAccessById(id);
		appAccessRepository.delete(appAccess);
		return appAccess;
	}

	@Override
	public AppAccess findAppAccessByApiKey(String apiKey) {
		Optional<AppAccess> appMobi = appAccessRepository.findByApiKey(apiKey);
		if(appMobi.isPresent()) {
			return appMobi.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy kết quả");
	}

	@Override
	public AppAccess createAppAccess(ReqAppAccessCreate reqAppAccessCreate, User creator) {
		AppAccess appAccess=new AppAccess();
		appAccess.setId(ObjectId.get());
		appAccess.setCreatedTime(new Date());
		appAccess.setUpdatedTime(new Date());
		appAccess.setApiKey(RandomStringUtils.randomAlphanumeric(64));
		appAccess.setStartTime(new Date(reqAppAccessCreate.getStartTime()));
		appAccess.setEndTime(new Date(reqAppAccessCreate.getEndTime()));
		appAccess.setName(reqAppAccessCreate.getName());
		appAccess.setDescription(reqAppAccessCreate.getDescription());
		if(creator!=null) {
			appAccess.setCreatorId(creator.getId());
			appAccess.setCreatorName(creator.getFullName());
		}
		appAccess.setActive(reqAppAccessCreate.isActive());
		appAccess.setIpsAccess(reqAppAccessCreate.getIpsAccess());
		appAccess.setOrganizationId(reqAppAccessCreate.getOrganizationId());
		
		try {
			return appAccessRepository.save(appAccess);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public AppAccess updateAppAccess(String appAccessId, ReqAppAccessUpdate reqAppAccessUpdate) {
		AppAccess appAccess=findAppAccessById(appAccessId);
		appAccess.setUpdatedTime(new Date());
		appAccess.setStartTime(new Date(reqAppAccessUpdate.getStartTime()));
		appAccess.setEndTime(new Date(reqAppAccessUpdate.getEndTime()));
		appAccess.setName(reqAppAccessUpdate.getName());
		appAccess.setDescription(reqAppAccessUpdate.getDescription());
		appAccess.setActive(reqAppAccessUpdate.isActive());
		appAccess.setIpsAccess(reqAppAccessUpdate.getIpsAccess());
		appAccess.setOrganizationId(reqAppAccessUpdate.getOrganizationId());
		
		return appAccessRepository.save(appAccess);
	}
}
