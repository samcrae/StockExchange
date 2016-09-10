package exceptions;

public class ConnectionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ConnectionException(){
		System.out.println("User is already subscribed for this message");
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public ConnectionException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
}
