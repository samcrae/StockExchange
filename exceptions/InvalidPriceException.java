/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 *@version 1.1 April 23 2015
 */

package exceptions;

public class InvalidPriceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidPriceException (){
		System.out.println("Invalid price entered");
	}
	
	public InvalidPriceException (String message){
	super(message);
	}
	
	public InvalidPriceException (String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidPriceException (String message, Exception e){
		super(message, e);
	}
}
