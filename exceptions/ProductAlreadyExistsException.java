package exceptions;

public class ProductAlreadyExistsException extends Exception{

private static final long serialVersionUID = 1L;
	
	public ProductAlreadyExistsException(){
		System.out.println("Product already exists");
	}

	public ProductAlreadyExistsException(String message) {
		super(message);
	}

	public ProductAlreadyExistsException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public ProductAlreadyExistsException(String message, Throwable throwable, Exception e){
		super(message, e);
	}
}
