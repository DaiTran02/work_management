package ws.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDatetimeLongValidator implements ConstraintValidator<ValidDatetimeLong, Long> {

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return true;
	}	
}
