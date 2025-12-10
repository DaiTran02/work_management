package com.ngn.api.utils;

import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class ApiConvertUtil {
	public static String convertToParams(Object object){
		if(object==null)
			return null;
		
        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(object, Map.class);

        StringBuilder qs = new StringBuilder();
        for (String key : map.keySet()){
            if (map.get(key) == null){
                continue;
            }
            qs.append(key);
            qs.append("=");
            qs.append(map.get(key));
            qs.append("&");
        }

        if (qs.length() != 0) {
            qs.deleteCharAt(qs.length() - 1);
        }
        return qs.toString();
    }
	
	public static <T,I> I copyDeep(T data, Class<I> type) throws Exception{
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(data), type);
	}
	
	public static <T,I> I jsonToModel(T data, Class<I> type) throws Exception{
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(data), type);
	}
	
	public static <T,I> I jsonToModelList(T data,Type listType) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(data), listType);
	}

}
