package ws.core.services;

import ws.core.model.request.ReqLoginByCodeCreate;

public interface LogLoginByCodeService {
	void saveLog(ReqLoginByCodeCreate reqLoginByCodeCreate);
}
