/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 */

package exceptions;

public class NotSubscribedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NotSubscribedException(){
		System.out.println("User not subscribed for this message");
	}

	public NotSubscribedException(String message) {
		super(message);
	}

	public NotSubscribedException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public NotSubscribedException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
