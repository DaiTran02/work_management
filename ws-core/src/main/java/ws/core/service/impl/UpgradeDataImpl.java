package ws.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.enums.DocAction;
import ws.core.enums.DocStatus;
import ws.core.model.Doc;
import ws.core.model.Organization;
import ws.core.model.Task;
import ws.core.model.Upgrade;
import ws.core.model.User;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.TaskService;
import ws.core.services.UpgradeDataService;
import ws.core.services.UpgradeService;
import ws.core.services.UserService;

@Service
public class UpgradeDataImpl implements UpgradeDataService{

	@Autowired
	private UpgradeService upgradeService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private DocService docService;
	
	@Override
	public void realtime() {
		System.out.println("Checking and upgrade data ...");
		if(!upgradeService.existsName("upgradeOrganization_20241125")) {
			if(upgradeOrganization_20241125()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradeOrganization_20241125");
				upgrade.setDescription("Cập nhật vai trò và nhóm trong đơn vị thêm thông tin: active, archive, id, ...");
				upgradeService.save(upgrade);
			}
		}
		
		if(!upgradeService.existsName("upgradeOrganization_20250107")) {
			if(upgradeOrganization_20250107()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradeOrganization_20250107");
				upgrade.setDescription("Cập nhật userName cho người dùng trong Đơn vị");
				upgradeService.save(upgrade);
			}
		}
		
		if(!upgradeService.existsName("upgradeTask_20250109")) {
			if(upgradeTask_20250109()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradeTask_20250109");
				upgrade.setDescription("Cập nhật field name follower thành followers trong database");
				upgradeService.save(upgrade);
			}
		}
		
		if(!upgradeService.existsName("upgradeLevelForOrganization_20250220")) {
			if(upgradeLevelForOrganization_20250220()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradeLevelForOrganization_20250220");
				upgrade.setDescription("Cập nhật level cho tổ chức");
				upgradeService.save(upgrade);
			}
		}
		
		
		if(!upgradeService.existsName("upgradeDocStatus_20250221")) {
			if(upgradeDocStatus_20250221()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradeDocStatus_20250221");
				upgrade.setDescription("Cập nhật status cho văn bản");
				upgradeService.save(upgrade);
			}
		}
		
		if(!upgradeService.existsName("upgradePositionNameUserOrganization_20250314")) {
			if(upgradePositionNameUserOrganization_20250314()) {
				Upgrade upgrade=new Upgrade();
				upgrade.setName("upgradePositionNameUserOrganization_20250314");
				upgrade.setDescription("Cập nhật position name cho user organization");
				upgradeService.save(upgrade);
			}
		}
		
		System.out.println("... done checking and upgrade data");
	}
	
	/**
	 * Cập nhật vai trò và nhóm trong đơn vị thêm thông tin: active, archive, id, ...
	 * @return
	 */
	private boolean upgradeOrganization_20241125() {
		System.out.println("Runing upgrade: upgradeOrganization_20241125 ....");
		List<Organization> organizations = organizationService.findOrganizationAll();
		for (Organization organization : organizations) {
			List<String> userIds=new ArrayList<>();
			List<UserOrganizationExpand> removes=new ArrayList<>();
			for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
				Optional<User> findUserOptional=userService.findUserById(userOrganizationExpand.getUserId());
				if(findUserOptional.isPresent()) {
					userOrganizationExpand.setFullName(findUserOptional.get().getFullName());
					userIds.add(userOrganizationExpand.getUserId());
				}else {
					removes.add(userOrganizationExpand);
				}
			}
			
			/* Xóa người dùng trong đơn vị nếu không tồn tại */
			for(UserOrganizationExpand userOrganizationExpand:removes) {
				organization.getUserOrganizationExpands().remove(userOrganizationExpand);
			}
			
			/* Xóa người dùng trong vai trò nếu không tồn tại trong đơn vị */
			for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
				List<String> userIdsRemove=new ArrayList<>();
				for(String userId:roleOrganizationExpand.getUserIds()) {
					if(!userIds.contains(userId)) {
						userIdsRemove.add(userId);
					}
				}
				
				for(String userIdRemove:userIdsRemove) {
					roleOrganizationExpand.getUserIds().remove(userIdRemove);
				}
			}
			
			/* Xóa người dùng trong nhóm nếu không tồn tại trong đơn vị */
			for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
				List<String> userIdsRemove=new ArrayList<>();
				for(String userId:groupOrganizationExpand.getUserIds()) {
					if(!userIds.contains(userId)) {
						userIdsRemove.add(userId);
					}
				}
				
				for(String userIdRemove:userIdsRemove) {
					groupOrganizationExpand.getUserIds().remove(userIdRemove);
				}
			}
			
			organizationService.save(organization);
		}
		
		System.out.println(".... done upgrade upgradeOrganization_20241125");
		return true;
	}

	/**
	 * Cập nhật thêm userName trong userOrganizationExpand của Đơn vị
	 * @return
	 */
	private boolean upgradeOrganization_20250107() {
		System.out.println("Runing upgrade: upgradeOrganization_20250107 ....");
		List<Organization> organizations = organizationService.findOrganizationAll();
		for (Organization organization : organizations) {
			for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
				Optional<User> findUserOptional=userService.findUserById(userOrganizationExpand.getUserId());
				if(findUserOptional.isPresent()) {
					User user=findUserOptional.get();
					
					/* Thêm dữ liệu userName cho Người dùng trong Đơn vị */
					userOrganizationExpand.setUserName(user.getUsername());
				}
			}
			
			organizationService.save(organization);
		}
		
		System.out.println(".... done upgrade upgradeOrganization_20250107");
		return true;
	}

	/**
	 * Cập nhật lại field name follower thành followers
	 * @return
	 */
	private boolean upgradeTask_20250109() {
		System.out.println("Runing upgrade: upgradeTask_20250109 ....");
		List<Task> tasks = taskService.findTaskAll(new TaskFilter());
		for (Task task : tasks) {
			taskService.saveTask(task, null, null);
		}
		System.out.println(".... done upgrade upgradeTask_20250109");
		return true;
	}
	
	/**
	 * Cập nhật thêm level cho Tổ chức
	 * @return
	 */
	private boolean upgradeLevelForOrganization_20250220() {
		System.out.println("Runing upgrade: upgradeLevelForOrganization_20250220 ....");
		List<Organization> organizations = organizationService.findOrganizationAll();
		for (Organization organization : organizations) {
			organization.setLevel(organizationService.autoDetectDefaultByUnitCode(organization.getUnitCode()).getKey());
			organizationService.save(organization);
		}
		System.out.println(".... done upgrade upgradeLevelForOrganization_20250220");
		return true;
	}
	
	private boolean upgradeDocStatus_20250221() {
		System.out.println("Runing upgrade: upgradeDocStatus_20250221 ....");
		List<Doc> docs = docService.findDocAll(new DocFilter());
		for (Doc doc : docs) {
			if(doc.getCountTask()>0) {
				doc.setStatus(DocStatus.dangthuchien);
			}
			docService.saveDoc(doc, DocAction.capnhat);
		}
		System.out.println(".... done upgrade upgradeDocStatus_20250221");
		return true;
	}
	
	private boolean upgradePositionNameUserOrganization_20250314() {
		System.out.println("Runing upgrade: upgradePositionNameUserOrganization_20250314 ....");
		List<User> users = userService.findUserAll();
		List<Organization> organizations=organizationService.findOrganizationAll();
		for (Organization organization : organizations) {
			System.out.println("Organization: "+organization.getName());
			for(UserOrganizationExpand userOrganizationExpand: organization.getUserOrganizationExpands()) {
				if(userOrganizationExpand.getPositionName()==null) {
					 Optional<User> findUser = users.stream().filter(e->e.getId().equals(userOrganizationExpand.getUserId())).findFirst();
					 if(findUser.isPresent()) {
						 userOrganizationExpand.setPositionName(findUser.get().getJobTitle());
						 System.out.println("-> Update user "+userOrganizationExpand.getFullName()+"/"+userOrganizationExpand.getPositionName());
					 }
				}
			}
			organizationService.save(organization);
		}
		System.out.println(".... done upgrade upgradePositionNameUserOrganization_20250314");
		return true;
	}
}
