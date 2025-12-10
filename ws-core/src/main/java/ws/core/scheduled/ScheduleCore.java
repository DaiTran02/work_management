package ws.core.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ws.core.services.PartnerApiService;
import ws.core.services.ScheduleCoreService;
import ws.core.services.redis.LdapDataUserServiceRD;

@Component
public class ScheduleCore {

	@SuppressWarnings("unused")
	@Autowired
	private ScheduleCoreService scheduleCoreService;
	
	@Autowired
	private LdapDataUserServiceRD ldapDataUserServiceRD;
	
	@Autowired
	private PartnerApiService partnerApiService;
	
	@Scheduled(cron = "0 */30 * ? * *") /* mỗi 30 phút lần */
    public void syncDataCore_UserInfo() {
		System.out.println("Cập nhật user-organization");
		try {
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					//scheduleCoreService.updateUserInfo();
				}
			});
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "0 */45 * ? * *") /* mỗi 45 phút lần */
    public void syncDataCore_CountTaskOfDoc() {
		System.out.println("Cập nhật count-task-of-doc");
		try {
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					//scheduleCoreService.updateCountTaskOfDoc();
				}
			});
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "0 */60 * ? * *") /* mỗi 60 phút lần */
    public void syncLdapDataUsers_StoreagesInRedis() {
		System.out.println("Cập nhật syncLdapDataUsers_StoreagesInRedis");
		try {
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					ldapDataUserServiceRD.initStoreDataUsers();
				}
			});
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "0 0 1 * * MON") /* 1 tuần 1 lần */
	public void syncDataOrgFromApiVNPT() {
		System.out.println("Update mapping");
		try {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					partnerApiService.doMappingOrg(null);
				}
			});
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
