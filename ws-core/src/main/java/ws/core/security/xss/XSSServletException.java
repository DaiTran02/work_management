package ws.core.security.xss;

@SuppressWarnings("serial")
public class XSSServletException extends RuntimeException{
	public XSSServletException() {
    }

    public XSSServletException(String message) {
        super(message);
    }
}
