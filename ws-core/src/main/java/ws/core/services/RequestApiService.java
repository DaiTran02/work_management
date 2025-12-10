package ws.core.services;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;

public interface RequestApiService {
	<R> HttpEntity<String> securityHeaders(String token,String body);
	<R> HttpEntity<MultiValuedMap<String, Object>> securityHeaderMultiValueMap(String token,LinkedMultiValueMap<String, Object> multiValueMap);
	
	<T> T get(String url,ParameterizedTypeReference<T> responseType);
	<T,R> T post(String url,ParameterizedTypeReference<T> responseType,R jsonObject);
	<T,R> T put(String url,ParameterizedTypeReference<T> responseType,R jsonOject);
	<T> T delete(String url,ParameterizedTypeReference<T> responseType);
}
