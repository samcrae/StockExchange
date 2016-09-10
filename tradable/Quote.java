package tradable;

import price.Price;
import exceptions.*;

/**
*Represents a BUY and Sell side of a desired stock transaction including Price and volume for a single user.  Quote represents separate transactions (both the BUY and SELL) so does not implement tradable.
*<p>
*@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
*@version 1.1 April 23 2015
*/
public class Quote {
	private String userName;
	private String productSymbol; 
	QuoteSide buy; 
	QuoteSide sell;

	//CONSTRUCTORS ---------------------//
	public Quote(String userName, String productSymbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidVolumeException {
		if (sellVolume < 0) throw new InvalidVolumeException ("Invalid SELL-side Volume: " + sellVolume);
		if (buyVolume < 0) throw new InvalidVolumeException ("Invalid BUY-side Volume: " + buyVolume);
		this.userName = userName; 
		this.productSymbol = productSymbol;
		this.buy = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, "BUY", true); 
		this.sell = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, "SELL", true);
	}
	
	//UTILITIES----------------------//
	public String toString(){
		return userName + " quote: " + productSymbol + " " + buy.getPrice() + " x " + buy.getOriginalVolume() + " (Original Vol: " 
			+ buy.getOriginalVolume() + ", CXL'd Vol: " + buy.getCancelledVolume() + ") [" + sell.getId()+ "] - "
			+" " + sell.getPrice() + " x " + sell.getOriginalVolume() + " (Original Vol: " 
			+ sell.getOriginalVolume() + ", CXL'd Vol: " + sell.getCancelledVolume() + ") [" + sell.getId()+ "]\n";
	}
	
	//GETTERS ---------------------//
	public String getUserName() {
		return userName;
	}

	public String getProductSymbol() {
		return productSymbol;
	}

	public QuoteSide getQuoteSide(String bookSide) { // UPDATED TO USE .EQUALS() AND REMOVED CREATION OF NEW BUY/SELL OBJECTS
		bookSide = bookSide.toUpperCase(); 
		try {
		if (!bookSide.equals("BUY") && !bookSide.equals("SELL")) throw new InvalidQuoteException();
		}catch (InvalidQuoteException e) {System.err.println("Bookside has invalid data");}
		if (bookSide.equals("SELL")) return sell; 
		return buy;
	}
}
