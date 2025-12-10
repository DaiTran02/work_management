package ws.core.advice;

@SuppressWarnings("serial")
public class BadRequestExceptionAdvice extends RuntimeException{
	
	public BadRequestExceptionAdvice(String message) {
		super(message);
	}
}
