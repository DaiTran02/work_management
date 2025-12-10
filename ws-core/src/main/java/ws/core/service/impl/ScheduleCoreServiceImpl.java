package ws.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.Doc;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.User.BelongOrganization;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.DocFilter;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.ScheduleCoreService;
import ws.core.services.UserService;

@Service
public class ScheduleCoreServiceImpl implements ScheduleCoreService{

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private DocService docService;
	
	@Override
	public void updateUserInfo() {
		try {
			List<Organization> organizations=organizationService.findOrganizationAll();
			List<User> users=userService.findUserAll();
			for (User user : users) {
				if(user.isActive() && !user.getUsername().equals("administrator")) {
					List<String> jobTitles= new ArrayList<String>();
					for(BelongOrganization belongOrganization:user.getBelongOrganizations()) {
						Optional<Organization> findOrg = organizations.stream().filter(e->e.getId().equals(belongOrganization.getOrganizationId())).findAny();
						if(findOrg.isPresent()) {
							Optional<UserOrganizationExpand> findUserOrgExpand=findOrg.get().getUserOrganizationExpand(user.getId());
							if(findUserOrgExpand.isPresent()) {
								if(findUserOrgExpand.get().isActive() && findUserOrgExpand.get().getPositionName()!=null) {
									jobTitles.add(findUserOrgExpand.get().getPositionName());
								}
							}
						}
					}
					
					if(jobTitles.size()>0) {
						user.setJobTitle(String.join(", ", jobTitles));
					}else {
						user.setJobTitle("");
					}
					userService.saveUser(user);
					System.out.println("Update user: "+user.getFullName()+" with jobTitles: "+user.getJobTitle());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateCountTaskOfDoc() {
		try {
			List<Doc> docs=docService.findDocAll(new DocFilter());
			for (Doc doc : docs) {
				doc.setCountTask((int)docService.getCountTask(doc.getId()));
				docService.saveDoc(doc, null);
				System.out.println("docId ["+doc.getId()+"]: "+doc.getCountTask());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
