/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 *@version 1.1 April 23 2015
 */

package exceptions;

public class InvalidQuoteException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidQuoteException() {
		System.out.println("Side must be 'Buy' or 'Sell'.");
	}

	public InvalidQuoteException(String message) {
		super(message);
	}
	
	public InvalidQuoteException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidQuoteException(String message, Exception e){
		super(message, e);
	}
}