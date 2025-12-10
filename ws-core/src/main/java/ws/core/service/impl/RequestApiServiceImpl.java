package ws.core.service.impl;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import ws.core.services.PropsService;
import ws.core.services.RequestApiService;

@Service
public class RequestApiServiceImpl implements RequestApiService{
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private PropsService propsService;

	@Override
	public <R> HttpEntity<String> securityHeaders(String token, String body) {
		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", token);
		header.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<String>(body,header);
	}

	@Override
	public <R> HttpEntity<MultiValuedMap<String, Object>> securityHeaderMultiValueMap(String token,
			LinkedMultiValueMap<String, Object> multiValueMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(String url, ParameterizedTypeReference<T> responseType) {
		ResponseEntity<T> responseEntity = restTemplate.exchange(propsService.getPartnerApiUrl()+url, HttpMethod.GET,securityHeaders(propsService.getPartnerApiKey(), ""),responseType);
		return responseEntity.getBody();
	}

	@Override
	public <T, R> T post(String url, ParameterizedTypeReference<T> responseType, R jsonObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, R> T put(String url, ParameterizedTypeReference<T> responseType, R jsonOject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T delete(String url, ParameterizedTypeReference<T> responseType) {
		// TODO Auto-generated method stub
		return null;
	}

}
