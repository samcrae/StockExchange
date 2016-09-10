/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 */

package exceptions;

public class AlreadySubscribedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AlreadySubscribedException(){
		System.out.println("User is already subscribed for this message");
	}

	public AlreadySubscribedException(String message) {
		super(message);
	}

	public AlreadySubscribedException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public AlreadySubscribedException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
