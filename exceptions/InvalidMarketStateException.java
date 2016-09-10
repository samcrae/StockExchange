package exceptions;

public class InvalidMarketStateException extends Exception{

	
private static final long serialVersionUID = 1L;
	
	public InvalidMarketStateException(){
		System.out.println("Invalid market state transition");
	}

	public InvalidMarketStateException(String message) {
		super(message);
	}

	public InvalidMarketStateException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidMarketStateException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
}
