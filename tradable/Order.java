
package tradable;

import price.*;
import exceptions.*;

/**
*Represents the BUY or SELL volume and price - or a Market Price request with desired volume  
*<p>
*@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
*@version 1.1 April 23 2015
*/

public class Order implements Tradable {
	private String userName; 
	private String productSymbol; 
	private Price orderPrice; 
	final private int originalVolume;
	private String buyOrSell;
	private String id;
	private int remainingVolume;
	private int cancelVolume;
	private boolean isQuote = false;
	
	//CONSTRUCTOR-------------------//
	public Order(String userName, String productSymbol, Price orderPrice,
			int originalVolume, String side) throws InvalidVolumeException {
		
		if (originalVolume <= 0) throw new InvalidVolumeException("Invalid Order Volume: " + originalVolume);
		this.userName = userName;
		this.productSymbol = productSymbol;
		this.orderPrice = orderPrice;
		this.originalVolume = originalVolume;
		this.id = userName + productSymbol + orderPrice + System.nanoTime();
		this.remainingVolume = originalVolume; 						
		this.buyOrSell = side.toUpperCase();
		this.cancelVolume = 0;
	}

	// UTILITIES----------------//
	public String toString() {
		return userName + " order: " + buyOrSell + " " + remainingVolume + " " + productSymbol + " at " + orderPrice + "(Original Vol: " + originalVolume + ", CXL'd Vol: " 
				+ cancelVolume + "), ID: " + id;
	}

	// GETTERS---------------//
	public String getProduct() {
		return (productSymbol);
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

	public String getSide() {
		return buyOrSell;
	}

	public boolean isQuote() {
		return isQuote;
	}

	public String getId() {
		return id;
	}
	
	public String getUser() {
		return userName;
	}
	
	public String getSide(Order o) throws InvalidQuoteException {
		if (isQuote) throw new InvalidQuoteException("Cannot retrieve the side of a Quote");
		return getSide();
	}

	// SETTERS-------------------//
	public void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException {
		int remainingVolume = originalVolume - newCancelledVolume;
		if (remainingVolume < 0) throw new InvalidVolumeException ("Requested new Cancelled Volume ("+newCancelledVolume+") exceeds"
				+ " the tradable's Original Volume ("+originalVolume+")"); 
		cancelVolume = newCancelledVolume;
	}

	public void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException {
		if (remainingVolume > originalVolume) throw new InvalidVolumeException ("Requested new Remaining Volume ("+newRemainingVolume+") exceeds the tradable's Original Volume ("+originalVolume+")");
		remainingVolume = newRemainingVolume;
	}
}