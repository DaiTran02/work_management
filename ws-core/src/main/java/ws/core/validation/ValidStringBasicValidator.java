package ws.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStringBasicValidator implements ConstraintValidator<ValidStringBasic, String> {

	//private String regexp="^[a-zA-Z0-9.\\s\\p{L}]{0,256}$";
	private String regexp="^[a-zA-Z0-9\\.\\,\\;\\'\\&\\*\\/\\\"(\\)\\-\\_\\+\\!\\%\\s\\p{L}]{0,25600}$";
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(Pattern.compile(regexp).matcher(value).find()) {
			return true;
		}
		return false;
	}	
}
