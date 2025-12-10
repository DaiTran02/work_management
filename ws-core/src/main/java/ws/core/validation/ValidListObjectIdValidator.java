package ws.core.validation;

import java.util.List;

import org.bson.types.ObjectId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidListObjectIdValidator implements ConstraintValidator<ValidListObjectId, List<String>> {

    @Override
    public void initialize(ValidListObjectId annotation) {

    }

	@Override
	public boolean isValid(List<String> values, ConstraintValidatorContext context) {
		if(values==null || (values!=null && values.size()==0))
			return true;
		
		for(String objectId:values) {
			if(!ObjectId.isValid(objectId)) {
				return false;
			}
		}
		return true;
	}
}
