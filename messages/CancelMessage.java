package messages;

import price.Price;
import exceptions.*;


/*******************************************************
 * @author Steven Mcrae, Briant Belcote, Tom Meier 
 * 
 *  Encapsulates data related to the cancellation of an order or
 *  quote-side by a user, or by the trading system. CancelMessage objects
 *  are immutable
 *******************************************************/
public class CancelMessage extends Message implements Comparable<CancelMessage> {

	/***************************************************************************
	 * @param user  The String username of the user whose order or quote-side is being cancelled
	 * @param product The string stock symbol that the cancelled order or quote-side was submitted for 
	 * @param price  The price specified in the cancelled order or quote-side
	 * @param volume The quantity of the order or quote-side that was cancelled
	 * @param details A text description of the cancellation
	 * @param side The side (BUY/SELL) of the cancelled order or quote-side
	 * @param id The String identifier of the cancelled order or quote-side
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 ***************************************************************************/
	public CancelMessage(String user, String product, Price price, int volume, String details, String side, String id)
			throws InvalidMessageException, InvalidVolumeException,InvalidPriceException {
			super(user, product, price, volume, details, side, id);
		}
	
	public int compareTo(CancelMessage cm)   {
		return getPrice().compareTo(cm.getPrice());
	}
	
	public String toString(){
		return "User: " + getUser() + ", Product: " + getProduct() + ", Price " + getPrice() + ", Volume: " + getVolume() + " Details: " + getDetails() + ", Side: " + getSide() + ", Id: " + getId();
	}
}
