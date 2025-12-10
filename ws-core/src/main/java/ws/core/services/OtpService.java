package ws.core.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ws.core.model.embeded.LdapUser;

@Service
public class OtpService {
	
	@Value("${ws.core.otp.enable}")
	private boolean enable;
	
	@Value("${ws.core.otp.url}")
	private String otpUrl;
	
	@Value("${ws.core.otp.xapikey}")
	private String xApiKey;
	
	private Gson gson = new Gson();
	
	public static void main(String args[]) {
		OtpService sv = new OtpService();
		sv.otpUrl="http://192.168.1.204:8093";
		sv.xApiKey="kgw5KynWd9MAGht7waCZsFI0eTumLPxLVUEnRVtEXjypXNkpQ3vHEtPvWK5llC4MDf4bvl0BsbedKaGFsnOPJOd8kpaTcW7R04FDsZZMOAwlyKwguh0LVEsi3qQnDJM3BkVeBjH4F2WYga6D3TGqKljEJSl7QyluPpq0+Rp0S+HYx07sp4Wjynvaxb4oRB14";
		
//		System.out.println(sv.check("dungqa"));
//		System.out.println(sv.authCode("dungqa","800805"));
//		System.out.println(sv.sendSmsOtpCode("dungqa"));
//		System.out.println(sv.authLdap("nguyenthingoclan","P@ssw0rd"));
//		System.out.println(sv.addUser("buinhukhue","buinhukhue@ngn.com.vn","0349130573","buinhukhue"));
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public JsonObject addUser(String userId, String email, String phone, String fullname){
		String url="";
		if(otpUrl.isBlank()) return null;
		url=otpUrl+"/user/add";
		System.out.println(url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		JsonObject jsonBodyRequest = new JsonObject();

        jsonBodyRequest.addProperty("userId", userId);
        jsonBodyRequest.addProperty("email", email);
        jsonBodyRequest.addProperty("phone", phone);
        jsonBodyRequest.addProperty("fullname", fullname);
        
        System.out.println("body: "+jsonBodyRequest.toString());
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .addHeader("Content-Type", "application/json")
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			return jsonResponse;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check if user using otp
	 * @param userId
	 * @return true / false
	 */
	public boolean check(String userId) {
		String url="";
		if(otpUrl.isBlank()) return false;
		url=otpUrl+"/user/checkOtp";
		System.out.println(url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
        System.out.println("body: "+jsonBodyRequest.toString());
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .addHeader("Content-Type", "application/json")
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			if(jsonResponse.get("status").getAsInt()==200) {
				return jsonResponse.get("data").getAsJsonObject().get("enableOtp").getAsBoolean();
			}else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Authen userId and otpCode
	 * @param userId, otpCode
	 * @return true / false
	 */
	public boolean authCode(String userId, String otpCode) {
		String url="";
		if(otpUrl.isBlank()) return false;
		url=otpUrl+"/user/authOtpCode";
		System.out.println("check otp url: "+url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
        jsonBodyRequest.addProperty("otpCode", otpCode);
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			if(jsonResponse.get("status").getAsInt()==200) {
				return true;
			}else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Authen user through ldap
	 * @param userId
	 * @param password
	 * @return
	 */
	public Optional<LdapUser> authLdap(String userId, String password) {
		String url="";
		if(otpUrl.isBlank()) 
			return Optional.empty();
		url=otpUrl+"/user/authLdap";
		System.out.println("check otp url: "+url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		if (userId.contains("@")) {
			userId = userId.substring(0, userId.indexOf("@"));
		}
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
        jsonBodyRequest.addProperty("password", password);
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			if(jsonResponse.get("status").getAsInt()==200) {
				LdapUser ldapUser=new LdapUser();
				JsonObject data = jsonResponse.getAsJsonObject("data");
				
				if(data.has("uid")) {
					ldapUser.setUsername(data.get("uid").getAsString());
				}
				
				if(data.has("cn")) {
					ldapUser.setFullName(data.get("cn").getAsString());
				}
				
				if(data.has("mail")) {
					ldapUser.setMail(data.get("mail").getAsString());
				}
				
				if(data.has("mobile")) {
					ldapUser.setMobile(data.get("mobile").getAsString());
				}
				
				if(data.has("userPrincipalName")) {
					ldapUser.setUserPrincipalName(data.get("userPrincipalName").getAsString());
				}
				
				if(ldapUser.isValid()) {
					return Optional.ofNullable(ldapUser);
				}
				return Optional.empty();
			}else {
				return Optional.empty();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	/**
	 * send otp code to user through sms
	 * @param userId
	 * @return
	 */
	public String sendSmsOtpCode(String userId) {
		String url="";
		if(otpUrl.isBlank()) return "url not found";
		url=otpUrl+"/user/sendSmsOtpCode";
		System.out.println("sendSmsOtpCode otp url: "+url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(response.string(), JsonObject.class);
			return jsonObject.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			return "send sms fail";
		}
	}
	
	public Optional<LdapUser> getUserByAccountName(String userId) {
		String url="";
		if(otpUrl.isBlank()) 
			return Optional.empty();
		url=otpUrl+"/user/getByUsernameInLdap";
		System.out.println("check otp url: "+url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		if (userId.contains("@")) {
			userId = userId.substring(0, userId.indexOf("@"));
		}
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
 
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			if(jsonResponse.get("status").getAsInt()==200) {
				LdapUser ldapUser=new LdapUser();
				JsonObject data = jsonResponse.getAsJsonObject("data");
				
				/* Kiểm tra data có thể rỗng */
				if(!data.isEmpty()) {
					if(data.has("uid")) {
						ldapUser.setUsername(data.get("uid").getAsString());
					}
					
					if(data.has("cn")) {
						ldapUser.setFullName(data.get("cn").getAsString());
					}
					
					if(data.has("mail")) {
						ldapUser.setMail(data.get("mail").getAsString());
					}
					
					if(data.has("mobile")) {
						ldapUser.setMobile(data.get("mobile").getAsString());
					}
					
					if(data.has("userPrincipalName")) {
						ldapUser.setUserPrincipalName(data.get("userPrincipalName").getAsString());
					}
					
					if(ldapUser.isValid()) {
						return Optional.ofNullable(ldapUser);
					}
				}
				return Optional.empty();
			}else {
				return Optional.empty();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public List<LdapUser> searchUserByAccountName(String userId) {
		String url="";
		if(otpUrl.isBlank()) 
			return List.of();
		url=otpUrl+"/user/searchInLdap";
		System.out.println("check otp url: "+url);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // Thời gian chờ khi kết nối
				.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // Thời gian chờ khi đọc dữ liệu
				.writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		
		if (userId.contains("@")) {
			userId = userId.substring(0, userId.indexOf("@"));
		}
		
		JsonObject jsonBodyRequest = new JsonObject();
        jsonBodyRequest.addProperty("userId", userId);
 
        List<LdapUser> results=new ArrayList<>();
		RequestBody body = RequestBody.create(mediaType, jsonBodyRequest.toString());
		Request request = new Request.Builder()
				  .url(url)
				  .method("POST", body)
				  .addHeader("X-API-KEY",xApiKey )
				  .build();
		ResponseBody response;
		try {
			response = client.newCall(request).execute().body();
			JsonObject jsonResponse = gson.fromJson(response.string(), JsonObject.class);
			System.out.println(jsonResponse.toString());
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray dataUsers = jsonResponse.getAsJsonArray("data");
				
				for (JsonElement dataUser : dataUsers) {
					JsonObject data = dataUser.getAsJsonObject();
					LdapUser ldapUser=new LdapUser();
					
					/* Kiểm tra data có thể rỗng */
					if(!data.isEmpty()) {
						if(data.has("uid")) {
							ldapUser.setUsername(data.get("uid").getAsString());
						}
						
						if(data.has("cn")) {
							ldapUser.setFullName(data.get("cn").getAsString());
						}
						
						if(data.has("mail")) {
							ldapUser.setMail(data.get("mail").getAsString());
						}
						
						if(data.has("mobile")) {
							ldapUser.setMobile(data.get("mobile").getAsString());
						}
						
						if(data.has("userPrincipalName")) {
							ldapUser.setUserPrincipalName(data.get("userPrincipalName").getAsString());
						}
						
						if(ldapUser.isValid()) {
							results.add(ldapUser);
						}
					}
				}
				return results;
			}else {
				return List.of();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
//	public Optional<LdapUser> getUserByAccountName(String accountName) {
//		LdapUser ldapUser=new LdapUser();
//		ldapUser.setUsername(accountName);
//		ldapUser.setMail(accountName+"@gmail.com");
//		ldapUser.setFullName(accountName);
//		ldapUser.setUserPrincipalName(accountName+"@tuhanoi.gov.vn");
//		ldapUser.setMobile("");
//		return Optional.ofNullable(ldapUser);
//	}
}
