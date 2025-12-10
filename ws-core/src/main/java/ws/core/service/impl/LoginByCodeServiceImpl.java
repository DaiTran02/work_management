package ws.core.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.LogLoginByCode;
import ws.core.model.request.ReqLoginByCodeCreate;
import ws.core.respository.LogLoginByCodeRepository;
import ws.core.services.LogLoginByCodeService;

@Service
public class LoginByCodeServiceImpl implements LogLoginByCodeService{
	@Autowired
	private LogLoginByCodeRepository loginByCodeRepository;

	@Override
	public void saveLog(ReqLoginByCodeCreate reqLoginByCodeCreate) {
		try {
			LogLoginByCode loginByCode = new LogLoginByCode();
			loginByCode.setFullName(reqLoginByCodeCreate.getFullName());
			loginByCode.setUsername(reqLoginByCodeCreate.getUsername());
			loginByCode.setDateLogin(new Date());
			loginByCodeRepository.save(loginByCode);
		} catch (Exception e) {
			
		}
	}

}
