package exceptions;

/** 
 * @author Steven McRae, Briant Becote, Tom Meier
 *	Throw exceptions for various invalid volume parameters
 */
public class InvalidMessageException extends Exception {

	
	
private static final long serialVersionUID = 1L;
	
	public InvalidMessageException(){
		System.out.println("Invalid message.");
	}

	public InvalidMessageException(String message) {
		super(message);
	}

	public InvalidMessageException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidMessageException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
