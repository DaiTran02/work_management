package ws.core.advice;

@SuppressWarnings("serial")
public class UnauthorizedExceptionAdvice extends RuntimeException{
	public UnauthorizedExceptionAdvice(String message){
		super(message);
	}
}
