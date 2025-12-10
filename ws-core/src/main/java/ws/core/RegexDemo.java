package ws.core;

public class RegexDemo {

	public static void main(String[] args) {
		String regexp="^[a-zA-Z0-9\\.\\,\\;\\'\\&\\*\\/\\\"(\\)\\-\\+\\!\\%\\s\\p{L}]{0,256}$";
//		String regexp="[a-zA-Z0-9\\.\\,\\;\\'\\&\\*\\/\\\"(\\)\\-\\+\\!\\%\\s\\p{L}]{0,256}";
		
		String text="*123abc";
		
		System.out.println("KQ: "+text.matches(regexp));
		
		System.out.println("nek "+"A26.01.1".split("\\.").length);
	}

}
