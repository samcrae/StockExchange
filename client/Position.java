package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



import exceptions.DataValidationException;
import exceptions.InvalidPriceException;
import price.Price;
import price.PriceFactory;

public class Position {

	/**store the holdings of the user. The key is the stock, the value is the number of
	shares they own**/
	private HashMap<String, Integer> holdings = new HashMap<>();
	/**
	 * hold the account costs for this user. This will keep a running balance between the money out for
		stock purchases, and the money in for stock sales.
	 */
	private Price accountCosts = PriceFactory.makeLimitPrice("$0.00");
	private HashMap<String, Price> lastSales = new HashMap<>();
	
	
	/***
	 * update the holdings list and the account costs when some market activity occurs
	 * 
	 * 
	 * @param product
	 * @param price
	 * @param side
	 * @param volume
	 * @throws DataValidationException 
	 * @throws InvalidPriceException 
	 */
	public void updatePosition(String product, Price price, String side, int volume) throws DataValidationException, InvalidPriceException {
		int adjustedVolume;
		
		if(side.equals("BUY")){
			adjustedVolume = volume;
		}
		else if(side.equals("SELL")) {
			adjustedVolume = -volume;
		}
		else {
			throw new DataValidationException("Side must be BUY or SELL");
		}
		
		if(!holdings.containsKey(product)) {
			holdings.put(product, adjustedVolume);
		}
		else {//hashmap has product
			int currentHoldingVolume = holdings.get(product);
			int resultingVolume = currentHoldingVolume + adjustedVolume;
			
			if(resultingVolume == 0) {//User no longer owns any of this stock
				holdings.remove(product);
			}
			else {
				holdings.put(product, resultingVolume);
			}
		}
		
		Price totalPrice = price.multiply(volume);
		
		if(side.equals("BUY")) {
			accountCosts = accountCosts.subtract(totalPrice);
		}
		else {//SELL
			accountCosts = accountCosts.add(totalPrice);
		}
	}
	
	/**
	 * insert the last sale for the specified stock into the last sales HashMap
	 * 
	 * @param product
	 * @param price
	 */
	public void updateLastSale(String product, Price price){
		lastSales.put(product, price);
	}
	
	/**
	 * @param product
	 * @return volume of the specified stock this user
		owns, or zero if user does not own stock
	 */
	public int getStockPositionVolume(String product) {
		
		if(!holdings.containsKey(product)) {
			return 0;
		}
		return holdings.get(product);
	}
	
	
	/**
	 * @return sorted ArrayList of Strings containing the stock
		symbols this user owns
	 */
	public ArrayList<String> getHoldings() {
		ArrayList<String> userStockSymbols = new ArrayList<>(holdings.keySet());
		Collections.sort(userStockSymbols);
		return userStockSymbols;
	}
	
	/**
	 * @param product
	 * @return current value of the stock symbol
		passed in that is owned by the user
	 * @throws InvalidPriceException 
	 */
	public Price getStockPositionValue(String product) throws InvalidPriceException {
		if(!holdings.containsKey(product)) {
			return PriceFactory.makeLimitPrice("$0.00");
		}
		
		//User does own stock
		Price lastSale = lastSales.get(product);
		
		if(lastSale == null) {//No current last sale for this stock
			return PriceFactory.makeLimitPrice("$0.00");
		}
		
		Price positionValue = lastSale.multiply(holdings.get(product));
		return positionValue;
	}

	public Price getAccountCosts() {
		return accountCosts;
	}
	
	
	/**
	 * @return total current value of all stocks this user owns
	 * @throws InvalidPriceException 
	 */
	public Price getAllStockValue() throws InvalidPriceException {
		
		ArrayList<String> userHoldings = getHoldings();
		long totalValue = 0;

		for (String stock : userHoldings) {
			totalValue += getStockPositionValue(stock).getPrice();
		}
		return PriceFactory.makeLimitPrice(totalValue);
	}
	
	
	/**
	 * 
	 * @return total current value of all stocks this user owns PLUS
		the account costs
	 * @throws InvalidPriceException 
	 */
	public Price getNetAccountValue() throws InvalidPriceException {
		return getAllStockValue().add(getAccountCosts());
	}	
}
