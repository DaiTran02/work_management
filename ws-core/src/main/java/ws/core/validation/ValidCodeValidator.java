package ws.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCodeValidator implements ConstraintValidator<ValidCode, String> {

	private String regexp="^[a-zA-Z0-9]{0,256}$";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value==null || (value!=null && Pattern.compile(regexp).matcher(value).find())) {
			return true;
		}
		return false;
	}	
}
