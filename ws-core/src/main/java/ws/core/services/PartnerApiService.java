package ws.core.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ws.core.model.User;
import ws.core.model.request.ReqPartnerReponseApi;
import ws.core.model.request.ReqPartnerTreeUnit;
import ws.core.model.request.ReqPartnerUnit;
import ws.core.model.request.ReqPartnerUser;

public interface PartnerApiService {
	ReqPartnerReponseApi<List<ReqPartnerUnit>> getAllUnits();
	ReqPartnerReponseApi<ReqPartnerUnit> getOneUnit(String identifier);
	ReqPartnerReponseApi<ReqPartnerUser> getUserByIdentifier(String identifier);
	ReqPartnerReponseApi<List<ReqPartnerTreeUnit>> getTreeUnitByIdentifier(String identifier);
	
	String doMappingOrg(User user);
	String doMappdingOrgByIdOrg(String idOrg,User user);
	CompletableFuture<String> doMappdingCustom(User user);
}
