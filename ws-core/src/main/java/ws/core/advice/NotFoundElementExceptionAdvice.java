package ws.core.advice;

@SuppressWarnings("serial")
public class NotFoundElementExceptionAdvice extends RuntimeException{
	
	public NotFoundElementExceptionAdvice(String message) {
		super(message);
	}
}
