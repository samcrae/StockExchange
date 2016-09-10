/**
 * 
 */
package tradeProcessor;

import java.util.HashMap;
import tradable.Tradable;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import messages.FillMessage;

/*******************************************************
 *  * Defines functionality needed to execute
 * trades between Tradable objects in this book side
 * @author Steven Mcrae, Briant Belcote, Tom Meier 
 ******************************************************/

public interface TradeProcessor {

	/**
	 * Called when it has been determined that a Tradable
	 *can trade against the content of the book
	 * @param trd
	 * @return
	 * @throws InvalidPriceException
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 */
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidPriceException, InvalidMessageException, InvalidVolumeException;
}
