package ws.core.validation;

import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
	private String[] allowedMimeTypes;
	
    private String[] allowedExtensions;
	
	@Override
	public void initialize(ValidFile constraintAnnotation) {
		this.allowedMimeTypes = constraintAnnotation.allowedMimeTypes();
        this.allowedExtensions = constraintAnnotation.allowedExtensions();
	}
	
	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if (file == null || file.isEmpty()) {
			log.info("file is null/empty");
            return true; // Let @NotNull handle this case
        }

        // Kiểm tra MIME type
        String contentType = file.getContentType();
        boolean validMimeType = Arrays.stream(allowedMimeTypes)
                .anyMatch(type -> type.equalsIgnoreCase(contentType));
        log.info("This file has contentType ["+contentType+"] and valid is "+validMimeType);
        
        // Kiểm tra extension
        String originalFilename = file.getOriginalFilename();
        boolean validExtension = originalFilename != null && 
                Arrays.stream(allowedExtensions)
                .anyMatch(ext -> originalFilename.toLowerCase().endsWith("." + ext.toLowerCase()));
        log.info("This file has originalFilename ["+originalFilename+"] and valid is "+validExtension);
        
        // Chấp nhận nếu thỏa mãn một trong hai hoặc cả hai
        return validMimeType || validExtension;
	}
}
