package ws.core.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.embeded.UserOrganizationExpand.AddFromSource;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqOrganizationCreate;
import ws.core.model.request.ReqOrganizationUpdate;
import ws.core.model.request.ReqPartnerReponseApi;
import ws.core.model.request.ReqPartnerTreeUnit;
import ws.core.model.request.ReqPartnerUnit;
import ws.core.model.request.ReqPartnerUser;
import ws.core.model.request.ReqUserCreate;
import ws.core.services.OrganizationService;
import ws.core.services.PartnerApiService;
import ws.core.services.RequestApiService;
import ws.core.services.UserService;

@Slf4j
@Service
public class PartnerApiServiceImpl implements PartnerApiService{
	
	@Autowired
	private RequestApiService requestApiService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrganizationService organizationService;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private User admin = null;
	

	@Override
	public ReqPartnerReponseApi<List<ReqPartnerUnit>> getAllUnits() {
		return requestApiService.get("/v1/Unit/GetUnits", new ParameterizedTypeReference<>() {});
	}

	@Override
	public ReqPartnerReponseApi<ReqPartnerUnit> getOneUnit(String identifier) {
		return requestApiService.get("/v1/Unit/GetUnitInfo?Identifier="+identifier, new ParameterizedTypeReference<>() {});
	}

	@Override
	public ReqPartnerReponseApi<ReqPartnerUser> getUserByIdentifier(String identifier) {
		return requestApiService.get("/v1/User/GetUserByUnit?Identifier="+identifier, new ParameterizedTypeReference<>() {});
	}
	
	@Override
	public ReqPartnerReponseApi<List<ReqPartnerTreeUnit>> getTreeUnitByIdentifier(String identifier) {
		return requestApiService.get("/v1/Unit/GetTreeUnitByParent?identifier="+identifier, new ParameterizedTypeReference<>() {});
	}
	

	@Override
	public String doMappingOrg(User user) {
		try {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ReqPartnerReponseApi<List<ReqPartnerUnit>> data = getAllUnits();
					if(data.isSuccess()) {
						if(user == null) {
							admin = findUserInDatabase("administrator");
						}
						data.getData().forEach(model->{
							String[] paths = model.getIdentifier().split("\\.");
							System.out.println("Data: "+model.getName()+"Check: "+paths.length);
							String unitRoot = paths.length > 1 ? paths[0] : "hell nah";
							System.out.println("Root: "+unitRoot);
							Organization org = findOrgInDatabase(unitRoot);
							if(org != null) {
								System.out.println("Check 1 ");
								System.out.println("Org name: "+org.getName());
								doMap(model, user == null ? admin : user, org.getId());
							}else {
								System.out.println("Check 2 ");
								doMap(model, user == null ? admin : user, null);
							}
						});
					}
					System.out.println("<======== Mappping successfull ========>");
					log.debug("<======== Mappping successfull ========>");
				}
			});
			
			thread.start();
			
			return "Đang xử lý";
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public String doMappdingOrgByIdOrg(String idOrg,User user) {
		try {
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Organization organization = organizationService.getOrganizationById(idOrg);
					ReqPartnerReponseApi<List<ReqPartnerTreeUnit>> dataTreeUnit = getTreeUnitByIdentifier(organization.getUnitCode());
					if(organization.getParentId() == null) {
						dataTreeUnit.getData().forEach(root->{
							if(root.getIdentifier().equals(organization.getUnitCode())) {
								doMapChild(root.getChildren(), user, organization.getId());
							}
						});
					}else {
						findEndUpdateOrg(dataTreeUnit.getData(), user, organization);
					}
					
					
					System.out.println("<======== Mappping successfull ========>");
					log.debug("<======== Mappping successfull ========>");
				}
			});
			
			thread.start();
			
			return "Đang xử lý";
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	@Override
	public CompletableFuture<String> doMappdingCustom(User user) {
		return CompletableFuture.supplyAsync(()->{
			try {
				ReqPartnerReponseApi<List<ReqPartnerUnit>> data = getAllUnits();
				if(data.isSuccess()) {
					if(user == null) {
						admin = findUserInDatabase("administrator");
					}
					data.getData().forEach(model->{
						String[] paths = model.getIdentifier().split("\\.");
						System.out.println("Data: "+model.getName()+"Check: "+paths.length);
						String unitRoot = paths.length > 1 ? paths[0] : "hell nah";
						System.out.println("Root: "+unitRoot);
						Organization org = findOrgInDatabase(unitRoot);
						if(org != null) {
							System.out.println("Check 1 ");
							System.out.println("Org name: "+org.getName());
							doMap(model, user == null ? admin : user, org.getId());
						}else {
							System.out.println("Check 2 ");
							doMap(model, user == null ? admin : user, null);
						}
					});
				}
				log.debug("<======== Mappping successfull ========>");
				return "Xử lý thành công";
			} catch (Exception e) {
				throw e;
			}
		},executorService);
	}
	
	@PreDestroy
    public void shutdown() {
        log.info("Shutting down ExecutorService");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("ExecutorService did not terminate within timeout, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted during ExecutorService shutdown", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
	
	
	private void findEndUpdateOrg(List<ReqPartnerTreeUnit> listData,User user,Organization target) {
		for(ReqPartnerTreeUnit item : listData) {
			if(item.getIdentifier().equals(target.getUnitCode())) {
				System.out.println("Da tim thay don vi: "+target.getName());
				
				ReqPartnerReponseApi<ReqPartnerUnit> info = getOneUnit(item.getIdentifier());
				
				ReqOrganizationUpdate reqOrganizationUpdate = new ReqOrganizationUpdate();
				reqOrganizationUpdate.setDescription(target.getDescription());
				reqOrganizationUpdate.setActive(target.isActive());
				reqOrganizationUpdate.setLevel(target.getLevel().getKey());
				reqOrganizationUpdate.setName(info.getData().getName());
				reqOrganizationUpdate.setOrder(target.getOrder());
				reqOrganizationUpdate.setOrganizationCategoryId(target.getOrganizationCategoryId());
				reqOrganizationUpdate.setUnitCode(info.getData().getIdentifier());
				reqOrganizationUpdate.setParentId(target.getParentId());
				
				Organization orgUpdate =  organizationService.updateOrganization(target.getId(), reqOrganizationUpdate);
				doImportUserToOrg(orgUpdate, user);
				doMapChild(item.getChildren(), user, target.getId());
				return;
			}else {
				findEndUpdateOrg(item.getChildren(), user, target);
			}
		}
	}
	
	
	private void doMap(ReqPartnerUnit reqPartnerUnit,User user,String idParent) {
		Organization orgFindInDataBase = findOrgInDatabase(reqPartnerUnit.getIdentifier());
		if(orgFindInDataBase != null) {
			
				try {
					ReqPartnerReponseApi<List<ReqPartnerTreeUnit>> dataTreeUnit = getTreeUnitByIdentifier(orgFindInDataBase.getUnitCode());
					if(dataTreeUnit.isSuccess()) {
						System.out.println("=============> Cập nhật đơn vị <===============");
						dataTreeUnit.getData().forEach(root->{
							if( idParent != null && !orgFindInDataBase.getId().equals(idParent) && root.getIdentifier().equals(orgFindInDataBase.getUnitCode())) {
								ReqOrganizationUpdate reqOrganizationUpdate = new ReqOrganizationUpdate();
								reqOrganizationUpdate.setDescription(orgFindInDataBase.getDescription());
								reqOrganizationUpdate.setActive(orgFindInDataBase.isActive());
								reqOrganizationUpdate.setLevel(orgFindInDataBase.getLevel().getKey());
								reqOrganizationUpdate.setName(root.getName());
								reqOrganizationUpdate.setOrder(orgFindInDataBase.getOrder());
								reqOrganizationUpdate.setOrganizationCategoryId(orgFindInDataBase.getOrganizationCategoryId());
								reqOrganizationUpdate.setUnitCode(root.getIdentifier());
								reqOrganizationUpdate.setParentId(idParent);
								
								Organization orgUpdate =  organizationService.updateOrganization(orgFindInDataBase.getId(), reqOrganizationUpdate);
								doImportUserToOrg(orgUpdate, user);
							}else {
								if(root.getIdentifier().equals(orgFindInDataBase.getUnitCode())) {
									ReqOrganizationUpdate reqOrganizationUpdate = new ReqOrganizationUpdate();
									reqOrganizationUpdate.setDescription(orgFindInDataBase.getDescription());
									reqOrganizationUpdate.setActive(orgFindInDataBase.isActive());
									reqOrganizationUpdate.setLevel(orgFindInDataBase.getLevel().getKey());
									reqOrganizationUpdate.setName(root.getName());
									reqOrganizationUpdate.setOrder(orgFindInDataBase.getOrder());
									reqOrganizationUpdate.setOrganizationCategoryId(orgFindInDataBase.getOrganizationCategoryId());
									reqOrganizationUpdate.setUnitCode(root.getIdentifier());
									reqOrganizationUpdate.setParentId(null);
									
									Organization orgUpdate =  organizationService.updateOrganization(orgFindInDataBase.getId(), reqOrganizationUpdate);
									doImportUserToOrg(orgUpdate, user);
								}
							}
							doMapChild(root.getChildren(), user, orgFindInDataBase.getId());
						});
					}
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
		}else {
			System.out.println("Tao don vi moi");
			try {
				ReqOrganizationCreate reqOrganizationCreate = new ReqOrganizationCreate();
				reqOrganizationCreate.setName(reqPartnerUnit.getName());
				reqOrganizationCreate.setDescription(reqPartnerUnit.getName());
				reqOrganizationCreate.setUnitCode(reqPartnerUnit.getIdentifier());
				reqOrganizationCreate.setParentId(idParent);
				
				Organization newCreateOrg = organizationService.createOrganization(reqOrganizationCreate, user);
				//Import user
				doImportUserToOrg(newCreateOrg, user);
				ReqPartnerReponseApi<List<ReqPartnerTreeUnit>> dataTreeUnit = getTreeUnitByIdentifier(newCreateOrg.getUnitCode());
				if(dataTreeUnit.isSuccess()) {
					dataTreeUnit.getData().forEach(model->{
						doMapChild(model.getChildren(), user, newCreateOrg.getId());
					});
				}
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
			
		}
	}
	
	private void doMapChild(List<ReqPartnerTreeUnit> dataTree,User user, String idParent) {
		dataTree.forEach(root1->{
			Organization checkRoot1 = findOrgInDatabase(root1.getIdentifier());
			if(checkRoot1 != null) {
				ReqOrganizationUpdate reqOrganizationUpdate = new ReqOrganizationUpdate();
				reqOrganizationUpdate.setDescription(checkRoot1.getDescription());
				reqOrganizationUpdate.setActive(checkRoot1.isActive());
				reqOrganizationUpdate.setLevel(checkRoot1.getLevel().getKey());
				reqOrganizationUpdate.setName(root1.getName());
				reqOrganizationUpdate.setOrder(checkRoot1.getOrder());
				reqOrganizationUpdate.setOrganizationCategoryId(checkRoot1.getOrganizationCategoryId());
				reqOrganizationUpdate.setUnitCode(root1.getIdentifier());
				reqOrganizationUpdate.setParentId(idParent);
				Organization orgUpdate = organizationService.updateOrganization(checkRoot1.getId(), reqOrganizationUpdate);
				doImportUserToOrg(orgUpdate, user);
				if(!root1.getChildren().isEmpty()) {
					doMapChild(root1.getChildren(), user, orgUpdate.getId());
				}
			}else {
				ReqOrganizationCreate reqOrganizationCreate = new ReqOrganizationCreate();
				reqOrganizationCreate.setName(root1.getName());
				reqOrganizationCreate.setDescription(root1.getName());
				reqOrganizationCreate.setUnitCode(root1.getIdentifier());
				reqOrganizationCreate.setParentId(idParent);
				
				Organization newCreateOrg = organizationService.createOrganization(reqOrganizationCreate, user);
				doImportUserToOrg(newCreateOrg, user);
				if(!root1.getChildren().isEmpty()) {
					doMapChild(root1.getChildren(), user, newCreateOrg.getId());
				}
			}

		});
	}
	
	
	private void doImportUserToOrg(Organization org,User userCreator) {
		try {
			ReqPartnerReponseApi<ReqPartnerUser> getUserByIndentifier = getUserByIdentifier(org.getUnitCode());
			
			
			UserFilter userFilter = new UserFilter();
			userFilter.setIncludeOrganizationIds(Arrays.asList(org.getId()));
			List<User> listUserWasAdd = userService.findUserAll(userFilter);
			Set<String> listIdUserToAddOrg = new HashSet<String>();
			listUserWasAdd.forEach(oldUser->{
				listIdUserToAddOrg.add(oldUser.getId());
			});
			
			
			if(getUserByIndentifier.isSuccess()) {
				getUserByIndentifier.getData().getItems().forEach(user->{
					User findUser = findUserInDatabase(user.getUserName());
					if(findUser == null) {
						ReqUserCreate reqUserCreate = new ReqUserCreate();
						reqUserCreate.setFullName(user.getUserFullName());
						reqUserCreate.setUsername(user.getUserName());
						reqUserCreate.setJobTitle(user.getJobPositionName());
						reqUserCreate.setEmail("test@ngn.vn");
						reqUserCreate.setPhone("");
						reqUserCreate.setPassword("Abc@12345");
						reqUserCreate.setActive(true);
						User newUserCreate = userService.createUser(reqUserCreate, userCreator);
						if(newUserCreate != null)
							listIdUserToAddOrg.add(newUserCreate.getId());
					}else {
						listIdUserToAddOrg.add(findUser.getId());
					}
				});
				organizationService.addUsersToOrganization(org.getId(), listIdUserToAddOrg.stream().toList(), AddFromSource.partner);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Organization findOrgInDatabase(String identifier) {
		return organizationService.findOrganizationByUnitCode(identifier).orElse(null);
	}
	
	private User findUserInDatabase(String username) {
		return userService.findUserByUserName(username).orElse(null);
	}

}
