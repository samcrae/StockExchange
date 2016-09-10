/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 */

package exceptions;

public class InvalidBuysideException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidBuysideException(){
		System.out.println("User entered a null object");
	}

	public InvalidBuysideException(String message) {
		super(message);
	}

	public InvalidBuysideException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidBuysideException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
