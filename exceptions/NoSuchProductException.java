package exceptions;

public class NoSuchProductException extends Exception {

private static final long serialVersionUID = 1L;
	
	public NoSuchProductException(){
		System.out.println("NoSuchProductException");
	}

	public NoSuchProductException(String message) {
		super(message);
	}

	public NoSuchProductException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public NoSuchProductException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
	
}
