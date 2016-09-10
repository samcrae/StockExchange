package exceptions;

public class OrderNotFoundException extends Exception{
private static final long serialVersionUID = 1L;
	
	public OrderNotFoundException(){
		System.out.println("Order not found.");
	}

	public OrderNotFoundException(String message) {
		super(message);
	}

	public OrderNotFoundException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public OrderNotFoundException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
}
