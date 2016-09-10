package tradable;

import price.Price;
import exceptions.*;


/**
*Represents a BUY or Sell side of a Quote including Price and volume.  Implements the Tradable interface.
*<p>
*@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
*@version 1.1 April 23 2015
*/
public class QuoteSide implements Tradable {
	private String user;
	private String symbol;
	private Price orderPrice; 
	private int originalVolume;
	private String buyOrSell;
	private String id;
	private int remainingVolume;
	private int cancelVolume;
	private boolean isQuote;

	// CONSTRUCTORS ------------------//
	public QuoteSide(String userName, String productSymbol, Price sidePrice,
			int originalVolume, String side) {
		id = userName + productSymbol + System.nanoTime();
		user = userName;
		symbol = productSymbol;
		orderPrice = sidePrice;
		this.originalVolume = originalVolume;
		remainingVolume = originalVolume;
		buyOrSell = side.toUpperCase();
		cancelVolume = 0;
	}
	
	public QuoteSide(QuoteSide qs) {
		id = qs.getId();
		user = qs.getUser();
		symbol = qs.getProduct();
		this.originalVolume = qs.getOriginalVolume();
		remainingVolume = qs.getRemainingVolume();
		buyOrSell = qs.getSide();
		cancelVolume = qs.getCancelledVolume();
	}
	
	//Constructor specifically for desiganting a QuoteSide object is part of a Quote
	public QuoteSide(String userName, String productSymbol, Price sidePrice,
			int originalVolume, String side, boolean isQuote) {
		id = userName + productSymbol + System.nanoTime();
		user = userName;
		symbol = productSymbol;
		orderPrice = sidePrice;
		this.originalVolume = originalVolume;
		remainingVolume = originalVolume;
		buyOrSell = side.toUpperCase();
		cancelVolume = 0;
		this.isQuote = isQuote;
	}

	
	//UTILITIES ------------------//
	public String toString(){
		return orderPrice + " x " + originalVolume + " (Original Vol: " + originalVolume + ", CXL'D Vol: " + cancelVolume + ") [" + id + "]";
	}

	// GETTERS -----------------//
	public String getProduct() {
		return symbol;
	}

	public Price getPrice() {
		return orderPrice;
	}

	public int getOriginalVolume() {
		return originalVolume;
	}

	public int getRemainingVolume() {
		return remainingVolume;
	}

	public int getCancelledVolume() {

		return cancelVolume;
	}

	public String getUser() {
		return user;
	}

	public String getSide() {
		return buyOrSell;
	}

	public boolean isQuote() {
		return isQuote;
	}

	public String getId() {
		return id;
	}

	public String getSide(Order o) throws InvalidQuoteException {
		if (o.equals(null)) throw new InvalidQuoteException ("User passed null Order"); 
		return buyOrSell;
	}

	// SETTERS-------------------//
	public void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException {
		int remainingVolume = originalVolume - cancelVolume;
		if (remainingVolume < 0) throw new InvalidVolumeException ("Requested new Remaining Volume ("+remainingVolume+") plus the Cancelled Volume ("+newCancelledVolume+") exceeds"
				+ " the tradable's Original Volume ("+originalVolume+")");
		
		cancelVolume = newCancelledVolume;

	}

	public void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException {
		int cancelledVolume = originalVolume - newRemainingVolume;
		if (remainingVolume > originalVolume) throw new InvalidVolumeException ("Requested new Remaining Volume ("+newRemainingVolume+") plus the Cancelled Volume ("+cancelledVolume+") exceeds"
				+ " the tradable's Original Volume ("+originalVolume+")");
		remainingVolume = newRemainingVolume;

	}
}