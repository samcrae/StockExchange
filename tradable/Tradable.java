package tradable;

import exceptions.InvalidVolumeException;
import price.Price;

/**
*Interface for representing BUY or SELL requests for trade within the Stock Exchange program.  This interface provides all methods associated with objects that can be traded.
*<p>
*@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
*@version 1.1 April 23 2015
*/

public interface Tradable {
	
	String getProduct();
	
	Price getPrice();
	
	int getOriginalVolume();
	
	int getRemainingVolume();
	
	int getCancelledVolume();
	
	void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException;
	
	void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException;
	
	String getUser();
	
	String getSide();
	
	boolean isQuote();
	
	String getId();
}