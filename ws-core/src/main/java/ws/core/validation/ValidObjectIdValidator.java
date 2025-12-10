package ws.core.validation;

import org.bson.types.ObjectId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidObjectIdValidator implements ConstraintValidator<ValidObjectId, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value==null || (value!=null && ObjectId.isValid(value))) {
			return true;
		}
		return false;
	}	
}
