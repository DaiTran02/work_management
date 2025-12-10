package ws.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidFileValidator.class})
public @interface ValidFile {
	String message() default "Chỉ chấp nhận những tệp: pdf, doc, docx, xls, xlsx, png, jpg, jpeg, zip, rar";
	
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    String[] allowedMimeTypes() default {
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "image/png",
        "image/jpeg",
        "application/zip",
        "application/x-rar-compressed"
    };
    
    String[] allowedExtensions() default {
        "pdf",
        "doc",
        "docx",
        "xls",
        "xlsx",
        "png",
        "jpg",
        "jpeg",
        "zip",
        "rar"
    };
}
