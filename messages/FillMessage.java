package messages;

import price.Price;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;


/*****************************************************************
 *  @author Steven Mcrae, Briant Belcote, Tom Meier 
 *   encapsulates data related to the fill (trade) of an order or quote-side. FillMessages are immutable
 *****************************************************************/
public class FillMessage extends Message implements Comparable<FillMessage> {

	/***************************************************************************
	 * @param user The String username of the user whose order or quote-side was filled
	 * @param product  The string stock symbol that the filled order or quote-side was submitted for 
	 * @param price The price that the order or quote-side was filled at
	 * @param volume The quantity of the order or quote-side that was filled
	 * @param details A text description of the fill (trade)
	 * @param side The side (BUY/SELL) of the filled order or quote-side
	 * @param id  The String identifier of the filled order or quote-side
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 ***************************************************************************/
	public FillMessage(String user, String product, Price price, int volume, String details, String side, String id)
			throws InvalidMessageException, InvalidVolumeException,InvalidPriceException {
		super(user, product, price, volume, details, side, id);
	}
	
	public int compareTo(FillMessage fm) {
		return getPrice().compareTo(fm.getPrice());
	}

	public String toString(){
		return "User: " + getUser() + ", Product: " + getProduct() + " Fill Price: " + getPrice() + ", Fill Volume: " + getVolume() + " Details: " + getDetails() + ", Side: " + getSide();
	}
}
