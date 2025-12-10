package vn.com.ngn.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public interface CoreExchangeInterface {
	
	<R> HttpEntity<String> securityHeaders(String token,String body);
	<R> HttpEntity<MultiValueMap<String, Object>> securityHeadersMutiValueMap(String token,LinkedMultiValueMap<String, Object> mutiValueMap);
	
	<T> T get(String path,ParameterizedTypeReference<T> responseType) throws IOException;
	<T> T pageGet(String path,ParameterizedTypeReference<T> responseType,String beforeOrAfter,Integer skip,Integer limit) throws IOException;
	<T> List<T> getAsList(String path,ParameterizedTypeReference<T[]> responseType) throws IOException;
	<T> List<T> pagedGetAsList(String path,ParameterizedTypeReference<T[]> responseType,String beforeOrAfter,Integer skip,Integer limit) throws IOException;
	<T,R> T post(String path,ParameterizedTypeReference<T> responseType,R jsonObject) throws IOException;
	<T,R> T postFile(String path,ParameterizedTypeReference<T> responseType,R mapObject) throws IOException;
	<T,R> T put(String path,ParameterizedTypeReference<T> responseType,R jsonObject) throws IOException;
	<T> T delete(String path,ParameterizedTypeReference<T> responseType);

}
