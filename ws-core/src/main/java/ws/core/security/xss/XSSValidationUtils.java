package ws.core.security.xss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class XSSValidationUtils {
	private boolean debug=false;
	public final Pattern pattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.\\/?\\s\\w+]*$", Pattern.UNICODE_CHARACTER_CLASS);
	
	public static boolean isValidURL(String uri) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[]  urls=uri.split("\\/");

		Arrays.stream(urls).filter(e->!StringUtils.isEmpty(e)).forEach(url->{
			String val=String.valueOf(url);
			Matcher matcher = pattern.matcher(val);
			if (!matcher.matches()) {
				flag.set(true);
				return;
			}
		});
		return !flag.get();
	}
	
	public static boolean isValidURL(String uri, List<String> skipWords) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[]  urls=uri.split("\\/");

		Arrays.stream(urls).filter(e->!StringUtils.isEmpty(e)).forEach(url->{
			String val=String.valueOf(url);
			if(skipWords.stream().anyMatch(p->val.toLowerCase().contains(p.toLowerCase()))){
				flag.set(true);
				return;
			}
			Matcher matcher = pattern.matcher(val);
			if (!matcher.matches()) {
				flag.set(true);
				return;
			}
		});
		return !flag.get();
	}

	public static boolean isValidRequestParam(String param) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[] paramList=param.split("&");

		Arrays.stream(paramList).filter(e->!StringUtils.isEmpty(e)).forEach(url->{
			String val=String.valueOf(url);
			Matcher matcher = pattern.matcher(val);
			if (!matcher.matches()) {
				flag.set(true);
				return;
			}
		});
		return !flag.get();
	}

	public static boolean isValidRequestParam(String param, List<String> skipWords) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[] paramList=param.split("&");

		Arrays.stream(paramList).filter(e->!StringUtils.isEmpty(e)).forEach(url->{
			String val=String.valueOf(url);
			if(skipWords.stream().anyMatch(val::equalsIgnoreCase)){
				flag.set(true);
				return;
			}
			Matcher matcher = pattern.matcher(val);
			if (!matcher.matches()) {
				flag.set(true);
				return;
			}
		});
		return !flag.get();
	}

	public static boolean isValidBody(String body, List<String> skipWords) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[] urls = new String[] {body};
		try {
			Arrays.stream(urls).filter(e -> !StringUtils.isEmpty(e)).forEach(url -> {
				String val = String.valueOf(url);
				if(debug) {
					System.out.println("url ne: " + url);
				}
				
				Map<String, Object> mapping = jsonToMap(new JSONObject(val));
				mapping.forEach((key, value) -> {
					if(debug) {
						System.out.println("-> Key ["+key+"]: "+value);
					}
					if (skipWords.stream().anyMatch(String.valueOf(value)::equalsIgnoreCase)) {
						if(debug) {
							System.out.println("Bad char found!!!!!");
						}
						flag.set(true);
						return;
					}
					Matcher matcher = pattern.matcher(String.valueOf(value));
					if (!matcher.matches()) {
						if(debug) {
							System.out.println("=> ["+key+"]: Invalid char found!!!!!");
						}
						flag.set(true);
						return;
					}
				});
				
			});
		}catch(Exception ex){
			ex.printStackTrace();
			flag.set(true);
		}
		return !flag.get();
	}
	
	public static boolean isValidBody(String body) {
		AtomicBoolean flag= new AtomicBoolean(false);
		String[] urls = new String[] {body};
		try {
			Arrays.stream(urls).filter(e -> !StringUtils.isEmpty(e)).forEach(url -> {
				String val = String.valueOf(url);
				if(debug) {
					System.out.println("url ne: " + url);
				}
				
				Map<String, Object> mapping = jsonToMap(new JSONObject(val));
				mapping.forEach((key, value) -> {
					if(debug) {
						System.out.println("-> Key ["+key+"]: "+value);
					}
					Matcher matcher = pattern.matcher(String.valueOf(value));
					if (!matcher.matches()) {
						if(debug) {
							System.out.println("=> ["+key+"]: Invalid char found!!!!!");
						}
						flag.set(true);
						return;
					}
				});
				
			});
		}catch(Exception ex){
			ex.printStackTrace();
			flag.set(true);
		}
		return !flag.get();
	}
 
	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(json != JSONObject.NULL) {
			retMap = toMap(json,retMap);
		}
		return retMap;
	}

	public static Map<String, Object> toMap(JSONObject object, Map<String, Object> map) throws JSONException {
		Iterator<String> keysItr = object.keys();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if(value instanceof JSONArray) {
				value = toList(key,(JSONArray) value,map);
			}else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value,map);
			}else {
				map.put(key, value);
			}
		}
		return map;
	}

	public static List<Object> toList(String key, JSONArray array, Map<String, Object> map ) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if(value instanceof JSONArray) {
				value = toList(key,(JSONArray) value,map);
			}else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value,map);
			}else{
				String mapValue=String.valueOf(value);
				if(map.containsKey(key)){
					mapValue+=","+String.valueOf(map.get(key));
				}
				map.put(key, mapValue);
			}
			list.add(value);
		}
		return list;
	}

	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		if (object == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
	
    public static boolean isJSONObject(String jsonInString) {
    	try {
    		Gson gson=new Gson();
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(JsonSyntaxException ex) { 
            return false;
        }
    }
}
