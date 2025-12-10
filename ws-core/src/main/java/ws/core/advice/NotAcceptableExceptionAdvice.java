package ws.core.advice;

@SuppressWarnings("serial")
public class NotAcceptableExceptionAdvice extends RuntimeException{
	public NotAcceptableExceptionAdvice() {
    }

    public NotAcceptableExceptionAdvice(String message) {
        super(message);
    }
}
