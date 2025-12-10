package ws.core.advice;

@SuppressWarnings("serial")
public class DuplicateKeyExceptionAdvice extends RuntimeException{
	public DuplicateKeyExceptionAdvice(String message) {
		super(message);
	}
}
