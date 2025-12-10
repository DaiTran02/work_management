package ws.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.bson.Document;

import com.google.gson.Gson;

public class CommonUtil {

	public static Document filterFields(Document article, String fields) {
		Document document=new Document();
		String []fieldKeys=fields.split(",");
		for (String key : fieldKeys) {
			key=key.trim();
			if(article.containsKey(key)) {
				document.append(key, article.get(key));
			}
		}
		return document;
	}

	public static String insertString(String originalString, String stringToBeInserted, int index){
		String newString = originalString.substring(0, index + 1)
                + stringToBeInserted
                + originalString.substring(index + 1);
		return newString;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap sortByValues(HashMap map) { 
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		} 
		return sortedHashMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap revertByValues(HashMap map) { 
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		} 
		return sortedHashMap;
	}
	
	public static String randPassword(int length) {
		char[] SYMBOLS = "!@#$%^&*()".toCharArray();
		char[] LOWERCASE = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		char[] NUMBERS = "0123456789".toCharArray();
		char[] ALL_CHARS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		Random rand = new SecureRandom();
		char[] password = new char[length];

		password[0] = LOWERCASE[rand.nextInt(LOWERCASE.length)];
		password[1] = UPPERCASE[rand.nextInt(UPPERCASE.length)];
		password[2] = NUMBERS[rand.nextInt(NUMBERS.length)];
		password[3] = SYMBOLS[rand.nextInt(SYMBOLS.length)];

		for (int i = 4; i < length; i++) {
			password[i] = ALL_CHARS[rand.nextInt(ALL_CHARS.length)];
		}

		for (int i = 0; i < password.length; i++) {
			int randomPosition = rand.nextInt(password.length);
			char temp = password[i];
			password[i] = password[randomPosition];
			password[randomPosition] = temp;
		}
		return new String(password);
	}
	
	public static String toFilename(String fileName) {
		fileName=TextUtil.removeAccent(fileName).replaceAll(" ", "_").toLowerCase();
		fileName=fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		fileName=fileName.replaceAll("_{2,}", "_");
		return fileName;
	}
	
	public static String toSlug(String input) {
		//System.out.println("Input: "+input);
		
		/* Chuyển thành chữ thường */
		input = input.toLowerCase(Locale.ENGLISH);
		//System.out.println("Chuyển thành chữ thường: "+input);
		
		/* Xóa dấu */
		input = Normalizer.normalize(input, Form.NFD);
		input = Pattern.compile("[\u0300-\u036f]").matcher(input).replaceAll("");
		//System.out.println("Xóa dấu: "+input);
		
		/* Xóa ký tự đ Đ */
		input = Pattern.compile("[đĐ]").matcher(input).replaceAll("d");
		//System.out.println("Xóa đĐ: "+input);
		
		/* Xóa ký tự đặc biệt */
		input = Pattern.compile("([^0-9a-z-\\s])").matcher(input).replaceAll("");
		//System.out.println("Xóa ký tự đặc biệt: "+input);
		
		/* Thay khoảng trắng thành dấu - */
		input = Pattern.compile("(\\s+)").matcher(input).replaceAll("-");
		//System.out.println("Thay khoảng trắng thành dấu -: "+input);
		
		/* Xóa dấu - liên tiếp */
		input = Pattern.compile("-+").matcher(input).replaceAll("-");
		//System.out.println("Xóa dấu - liên tiếp: "+input);
		
		/* Xóa dấu - dư ở đầu & cuối */
		input = Pattern.compile("^-+|-+$").matcher(input).replaceAll("");
		//System.out.println("Xóa dấu - ở đầu & cuối: "+input);
		
		/* Xóa các ký tự la tinh */
		input = Pattern.compile("[^\\w-]").matcher(input).replaceAll("");
		//System.out.println("Xóa ký tự la tinh: "+input);
		
		/* Chuyển thành chữ thường */
		return input.toLowerCase(Locale.ENGLISH);
	}
	
	public static String sumString(String str) {
		StringTokenizer stringTokenizer = new StringTokenizer(str, " ");
		String result="";
	    while (stringTokenizer.hasMoreTokens()) {
	        String strToken = stringTokenizer.nextToken();
	        char[] ch = strToken.toUpperCase().toCharArray();
	        result+=ch[0];
	    }
	    return toSlug(result);
	}
	
	public static String getCollectionName(Class<?> cls) {
		String collectionName=null;
		try {
 			for (Annotation annotation : cls.getAnnotations()) {
 	            Class<? extends Annotation> type = annotation.annotationType();
 	            if(type.getSimpleName().equalsIgnoreCase("Document")) {
 	            	for (Method method : type.getDeclaredMethods()) {
	 	                Object value = method.invoke(annotation, (Object[])null);
	 	                if(method.getName().equalsIgnoreCase("collection")) {
	 	                	collectionName=value.toString();
	 	                	return collectionName;
	 	                }
	 	            }
 	            }
 	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectionName;
	}
	
	public static <T, I> I copy(T data, Class<I> type) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(data), type);
	}
}
