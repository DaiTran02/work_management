package ws.core.services.redis;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import ws.core.model.embeded.LdapUser;
import ws.core.services.OtpService;

@Service
public class LdapDataUserServiceRD {
	
	@Autowired
    private RedisTemplate<Object, Object> redisTemplate;
	
	@Autowired
	private OtpService otpService;
	
	private final String key="dataLdapUsers";
	
	public void storeDataUsers(List<LdapUser> dataLdapUsers) {
		redisTemplate.delete(key);
		if(dataLdapUsers!=null && dataLdapUsers.size()>0) {
			redisTemplate.opsForList().rightPushAll(key, dataLdapUsers.toArray());
		}
	}
	
	public void initStoreDataUsers() {
		storeDataUsers(otpService.searchUserByAccountName("*"));
	}
	
	public List<LdapUser> searchUsers(String keySearch, long skip, long limit){
		if(redisTemplate.hasKey(key)) {
			if(keySearch!=null && !keySearch.isEmpty()) {
				Predicate<Object> predicate = element -> {
		            if (element instanceof LdapUser) {
		                return ((LdapUser) element).getUsername().contains(keySearch);
		            }
		            return false;
		        };
				return redisTemplate.opsForList().range(key, 0, -1).stream().filter(predicate).map(e->(LdapUser)e).skip(skip).limit(limit).collect(Collectors.toList());
			}
			return redisTemplate.opsForList().range(key, 0, -1).stream().map(e->(LdapUser)e).skip(skip).limit(limit).collect(Collectors.toList());
		}
		return List.of();
	}
	
	public long countSearchUsers(String keySearch){
		if(redisTemplate.hasKey(key)) {
			if(keySearch!=null && !keySearch.isEmpty()) {
				Predicate<Object> predicate = element -> {
		            if (element instanceof LdapUser) {
		                return ((LdapUser) element).getUsername().contains(keySearch);
		            }
		            return false;
		        };
				return redisTemplate.opsForList().range(key, 0, -1).stream().filter(predicate).count();
			}
			return redisTemplate.opsForList().range(key, 0, -1).size();
		}
		return 0;
	}
}
