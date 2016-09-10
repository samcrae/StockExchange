/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 */

package exceptions;

public class NullObjectException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NullObjectException(){
		System.out.println("User entered a null object");
	}

	public NullObjectException(String message) {
		super(message);
	}

	public NullObjectException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public NullObjectException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
