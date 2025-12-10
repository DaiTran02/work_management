package vn.com.ngn.abstracts;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Data;
import vn.com.ngn.utils.PropUtils;
import vn.com.ngn.utils.SessionUtil;

@Data
public class CoreExchangeAbstract {
	private final String baseUrl = PropUtils.getCoreApiUrl();
	private final RestTemplate restTemplate = new RestTemplate();
	
	public String getTokenAccess() {
		return SessionUtil.getToken();
	}
	
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
	
	public String toJson(Object object) {
		try {
			return getObjectMapper().writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to serialize");
		}
	}
	
	public LinkedMultiValueMap<String, Object> toMutiValueMap(Object object) throws IllegalAccessException{
		LinkedMultiValueMap<String, Object> mutiValueMap = new LinkedMultiValueMap<String, Object>();
		Map<String, Object> map = toMap(object);
		
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			mutiValueMap.add(entry.getKey(), entry.getValue());
		}
		return mutiValueMap;
	}
	
	public Map<String, Object> toMap(Object object) throws IllegalAccessException{
		Map<String, Object> map = new HashMap<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(object));
		}
		
		return map;
	}

}
