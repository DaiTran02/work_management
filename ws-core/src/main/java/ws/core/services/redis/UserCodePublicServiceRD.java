package ws.core.services.redis;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import ws.core.advice.UnauthorizedExceptionAdvice;
import ws.core.model.data.UserCodePublic;
import ws.core.services.PropsService;
import ws.core.util.DateTimeUtil;

@Service
public class UserCodePublicServiceRD {
	@Autowired
	private PropsService propsService;
	
	@Autowired
    private RedisTemplate<Object, Object> redisTemplate;

	private String generationToken() {
		return RandomStringUtils.randomAlphanumeric(128, 256);
	}
	
    public UserCodePublic push(String username) {
		UserCodePublic userCodePublic=new UserCodePublic();
		userCodePublic.setUsername(username);
		userCodePublic.setToken(generationToken());
		userCodePublic.setExpired(DateTimeUtil.nextMinutes(new Date(), propsService.getSecurityAuthCodeExpiredMinutes()));
		redisTemplate.opsForValue().set(userCodePublic.getToken(), userCodePublic, propsService.getSecurityAuthCodeExpiredMinutes(), TimeUnit.MINUTES);
		return userCodePublic;
	}
    
    public UserCodePublic pull(String token) {
    	UserCodePublic userCodePublic = null;
    	if(propsService.isSecurityAuthCodeUseableTimes()) {
    		userCodePublic = (UserCodePublic) redisTemplate.opsForValue().get(token);
    	}else {
    		userCodePublic = (UserCodePublic) redisTemplate.opsForValue().getAndDelete(token);
    	}
		
		if(userCodePublic!=null) {
			return userCodePublic;
		}
		throw new UnauthorizedExceptionAdvice("Xác thực không thành công");
	}
	
	public Optional<UserCodePublic> find(String token) {
		return Optional.ofNullable((UserCodePublic)redisTemplate.opsForValue().get(token));
	}
}
