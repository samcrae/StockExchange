package exceptions;

public class DataValidationException extends Exception {

private static final long serialVersionUID = 1L;
	
	public DataValidationException(){
		System.out.println("Illegal Quote Price");
	}

	public DataValidationException(String message) {
		super(message);
	}

	public DataValidationException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public DataValidationException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
}
