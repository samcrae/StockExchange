/**
 * Throws exceptions and provides applicable messages based on the the constructor used in the exception call.
 *<p>
 *@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
 */

package exceptions;

public class InvalidVolumeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidVolumeException(){
		System.out.println("Volume must be greater than 0");
	}

	public InvalidVolumeException(String message) {
		super(message);
	}

	public InvalidVolumeException(String message, Throwable throwable){
		super(message, throwable);
	}
	
	public InvalidVolumeException(String message, Throwable throwable, Exception e){
		super(message, e);
	}

}
