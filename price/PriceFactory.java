package price;

import java.util.*;

/**
 * Globally accessible class based on the flyweight design pattern to control the creation of duplicate Price objects.  
 * <p>
 * PriceFactory utilizes a static variable marketPrice as the single Price object for Market Prices and a HashMap for 
 * inventorying and controlling the creation of tradeable objects.
 * <p>
 * HashMap <K, V> 
 * K = (key) Long value of Price object.
 * V = (value) Price object.
 *<p>
 *LIMIT PRICE constructors Generates a new Price from a string or long argument with a call to newPrice 
 *<p>
 *MARKET PRICE constructors  Generates a nonvalue Price object for immediate trading.  
 *Each call to makeMarketPrice returns the initiated Price from the static variable marketPrice
 *<p>
 *NEW PRICE method Tests if a Price has already been created, if so -return provides the Price object stored in the HashMap pricetable
 *Otherwise creates a new Price, adds it to the HashMap pricetable, and returns the new Price.
 *@author Team "Write Once - Run Everywhere"Steven McRae, Thomas Meier, Briant Becote 
 *@version 1.1 April 23 2015
 */
public class PriceFactory {
	private static HashMap <Long, Price> pricetable = new HashMap<Long, Price>();
	final private static Price marketPrice = new Price();

	//LIMIT PRICES ----------------//
	public static Price makeLimitPrice(String value){
		int decLocation = value.indexOf(".");
		if (decLocation == -1 ) value += "00";
		else if (decLocation == value.length() - 1)  value += "00";
		else if (decLocation == value.length() - 2)  value += "0";
		value = value.replaceAll("[$,.]", ""); 
		double decimalPrice = Double.parseDouble(value);
		return newPrice((long)(decimalPrice));
	}
	public static Price makeLimitPrice(long value){
		return newPrice(value);
	}
	
	//MARKET PRICE ------------------//
	public static Price makeMarketPrice(){
		return marketPrice;
	}
	
	//FACTORY METHOD ----------//
	public static Price newPrice(long value){
		Long valueLong = (Long) value;
		if (pricetable.containsKey(valueLong)) return pricetable.get(valueLong);
		Price a = new Price(value); 
		pricetable.put(valueLong, a);
		return a;
	}
}