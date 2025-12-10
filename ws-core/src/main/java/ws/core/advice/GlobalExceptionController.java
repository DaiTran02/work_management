package ws.core.advice;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ws.core.model.response.ResponseAPI;
import ws.core.security.xss.XSSServletException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(ExpiredJwtException.class)
	public Object handleExpiredJwtException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage("Token hết hạn truy cập dữ liệu");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler(SignatureException.class)
	public Object handleSignatureException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage("Token không hợp lệ");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public Object handleBadCredentialsException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.UNAUTHORIZED);
		responseCMS.setMessage("Thông tin xác thực không chính xác");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler(AccountStatusException.class)
	public Object handleAccountStatusException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage("Tài khoản đã bị khóa, không có quyền truy cập");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler({AccessDeniedException.class, AccessDeniedExceptionAdvice.class})
	public Object handleAccessDeniedException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage("Không được phép truy cập nguồn dữ liệu này");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		responseCMS.setMessage("Lỗi hệ thống, vui lòng liên hệ quản trị viên");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}

	@ExceptionHandler(BindException.class)
	public Object handleBindException(BindException ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		
		ArrayList<String> messages=new ArrayList<String>();
		Document errors=new Document();
		ex.getBindingResult().getFieldErrors().forEach(error->{
			errors.put(error.getField(), error.getDefaultMessage());
			messages.add(error.getDefaultMessage());
		});
		responseCMS.setMessage("Thông tin yêu cầu không hợp lệ");
		responseCMS.setResult(errors);
		return responseCMS.build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object handleInvalidArgument(MethodArgumentNotValidException ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		
		ArrayList<String> messages=new ArrayList<String>();
		Document errors=new Document();
		ex.getBindingResult().getFieldErrors().forEach(error->{
			errors.put(error.getField(), error.getDefaultMessage());
			messages.add(error.getDefaultMessage());
		});
		responseCMS.setMessage("Thông tin nhập liệu chưa đúng");
		responseCMS.setResult(errors);
		return responseCMS.build();
	}
	
	@ExceptionHandler(XSSServletException.class)
	public Object handleXSSException(HttpServletRequest request, Exception ex) {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
		
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
	
	@ExceptionHandler(MultipartException.class)
	public void handleMultipartException(MultipartException ex,HttpServletResponse response) throws IOException {
		ex.printStackTrace();
		log.debug(ex.getMessage(), ex.getCause());
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Please select a file");
    }
  
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException ex, HttpServletResponse response) throws IOException {
    	/*ResponseCMS responseCMS=new ResponseCMS();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		responseCMS.setMessage(ex.getMessage());
		
		response.getWriter().write(convertObjectToJson(responseCMS));
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType("application/json");*/
    	response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
    
    @ExceptionHandler(DuplicateKeyExceptionAdvice.class)
	public Object handleDuplicateKeyExceptionAdvice(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.CONFLICT);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(NotFoundElementExceptionAdvice.class)
	public Object handleNotFoundElementExceptionAdvice(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.NOT_FOUND);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(NotAcceptableExceptionAdvice.class)
	public Object handleNotAcceptableExceptionAdvice(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler({UnauthorizedExceptionAdvice.class, AuthenticationException.class})
	public Object handleUnauthorizedExceptionAdvice(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.UNAUTHORIZED);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(BadRequestExceptionAdvice.class)
	public Object handleBadRequestExceptionAdvice(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingServletRequestParameterException(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		responseCMS.setMessage("Request parameter yêu cầu bắt buộc");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(ObjectIdExceptionAdvance.class)
	public Object handleObjectIdExceptionAdvance(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		responseCMS.setMessage(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(IllegalArgumentException.class)
	public Object handleIllegalArgumentException(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		responseCMS.setMessage("Tham số không hợp lệ");
		responseCMS.setError(ex.getMessage());
		return responseCMS.build();
	}
    
    @ExceptionHandler(NoResourceFoundException.class)
	public Object handleNoResourceFoundException(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.NOT_FOUND);
		responseCMS.setMessage("Không tìm thấy đường dẫn");
		return responseCMS.build();
	}
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest request, Exception ex) {
    	ex.printStackTrace();
    	log.debug(ex.getMessage(), ex.getCause());
    	
		ResponseAPI responseCMS=new ResponseAPI();
		responseCMS.setStatus(HttpStatus.FORBIDDEN);
		responseCMS.setMessage("Phương thức không hỗ trợ");
		return responseCMS.build();
	}
    
}
