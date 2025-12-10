package ws.core.advice;

@SuppressWarnings("serial")
public class AccessDeniedExceptionAdvice extends RuntimeException{
	public AccessDeniedExceptionAdvice() {
    }

    public AccessDeniedExceptionAdvice(String message) {
        super(message);
    }
}
