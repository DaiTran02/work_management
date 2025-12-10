package vn.com.ngn.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import vn.com.ngn.abstracts.CoreExchangeAbstract;
import vn.com.ngn.interfaces.CoreExchangeInterface;

public class CoreExchangeService extends CoreExchangeAbstract implements CoreExchangeInterface{

	@Override
	public <R> HttpEntity<String> securityHeaders(String token, String body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<String>(body,headers);
	}

	@Override
	public <R> HttpEntity<MultiValueMap<String, Object>> securityHeadersMutiValueMap(String token,
			LinkedMultiValueMap<String, Object> mutiValueMap) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return new HttpEntity<MultiValueMap<String,Object>>(mutiValueMap,headers);
	}

	@Override
	public <T> T get(String path, ParameterizedTypeReference<T> responseType) throws IOException {
		try {
			ResponseEntity<T> responseEntity = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.GET, securityHeaders(getTokenAccess(), ""), responseType);
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw new IOException(e.getResponseBodyAsString());
		}
	}

	@Override
	public <T> T pageGet(String path, ParameterizedTypeReference<T> responseType, String beforeOrAfter, Integer skip,
			Integer limit) throws IOException {
		path +="?"+beforeOrAfter+"&skip="+skip+"&limit="+limit;
		return get(path, responseType);
	}

	@Override
	public <T> List<T> getAsList(String path, ParameterizedTypeReference<T[]> responseType) throws IOException {
		T[] result = get(path, responseType);
	       return result == null ? Collections.emptyList() : Arrays.asList(result);
	}


	@Override
	public <T, R> T post(String path, ParameterizedTypeReference<T> responseType, R jsonObject) throws IOException {
		String jsonBody = toJson(jsonObject);
		try {
			ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.POST,securityHeaders(getTokenAccess(), jsonBody), responseType);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw new IOException("");
		}
	}

	@Override
	public <T, R> T postFile(String path, ParameterizedTypeReference<T> responseType, R mapObject) throws IOException {
		try {
			LinkedMultiValueMap<String, Object> mutiValueMap = toMutiValueMap(mapObject);
			ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.POST,securityHeadersMutiValueMap(getTokenAccess(), mutiValueMap), responseType);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw new IOException(e.getResponseBodyAsString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public <T, R> T put(String path, ParameterizedTypeReference<T> responseType, R jsonObject) throws IOException {
		String jsonBody = toJson(jsonObject);
		ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.PUT,securityHeaders(getTokenAccess(), jsonBody), responseType);
		return response.getBody();
	}

	@Override
	public <T> T delete(String path, ParameterizedTypeReference<T> responseType) {
		ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.DELETE,securityHeaders(getTokenAccess(), ""),responseType);
		return response.getBody() == (null) ? null : response.getBody();
	}
	
	public <T,R> T deleteHaveObject(String path, ParameterizedTypeReference<T> responseType, R jsonObject) throws IOException{
		String jsonBody = toJson(jsonObject);
		ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path, HttpMethod.DELETE,securityHeaders(getTokenAccess(), jsonBody),responseType);
		return response.getBody();
		
	}

	public <T,R> T postUser(String path,ParameterizedTypeReference<T> responseType,R jsonObject) throws IOException {
		String jsonBody = toJson(jsonObject);
		ResponseEntity<T> response = getRestTemplate().exchange(getBaseUrl()+path,HttpMethod.POST,securityHeaders("", jsonBody), responseType);
		return response.getBody();

	}

	@Override
	public <T> List<T> pagedGetAsList(String path, ParameterizedTypeReference<T[]> responseType, String beforeOrAfter,
			Integer skip, Integer limit) throws IOException {
		return null;
	}

}
