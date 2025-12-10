package ws.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {

	private String regexp="^[a-zA-Z0-9._-]{0,256}$";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(Pattern.compile(regexp).matcher(value).find()) {
			return true;
		}
		return false;
	}	
}
