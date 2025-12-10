package ws.core.advice;

@SuppressWarnings("serial")
public class TokenRefreshException extends RuntimeException{
	public TokenRefreshException(String message) {
		super(message);
	}
}
