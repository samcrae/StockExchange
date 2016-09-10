

package price;

import java.text.DecimalFormat;
import java.util.ArrayList;

import exceptions.InvalidPriceException;

/**
 * Immutable object that defines currency/value (or nonvalue for MKT prices) for tradeable objects.
 * Values are stored in long form - the two places furthest to the right represent cents - decimal notation is provided in the stringPrice variable.
 * Prices may be limit Prices (standard value based Price) or Market Price
 * <p>
 * Market prices are nonvalue representations - used to indicate an order should process immediately.  These objects are constructed without a long value
 * parameter and maintained as a static variable in the PriceFactory class.
 *<p>
 *CONSTRUCTORS - no argument constructor creats a Market Price object.  With a long argument, creates a Limit Price object
 *<p>
 *MATH METHODS - Conduct basic math operations on two tradable objects.  Null objects and Market Prices throw applicable exceptions.
 *Price object argument in subtraction becomes the subtrahend: minuend - subtrahend = difference
 *<p>
 *COMPARISON METHODS - Operations to compare the long values associated with Tradable objects.  Null objects and Market Prices throw applicable exceptions.
 *Argument p is a Price object argument is the comparotee: comparotor > comparotee 
 *<p>
 *@author Team "Write Once - Run Everywhere"Steven McRae, Thomas Meier, Briant Becote 
 *@version 1.1 April 23 2015
 */
public class Price implements Comparable<Price> {
	private long price;
	private String stringPrice;
	private boolean isMarket;
	
	//CONSTRUCTORS---------------//
	//Constructor package visible to support Flyweight pattern with PriceFactory in same package
	Price(){
		this.stringPrice = "MKT";
		this.isMarket = true;
	}
	//Constructor package visible to support Flyweight pattern with PriceFactory in same package
	Price (long price){
		this.price = price;
		DecimalFormat a = new DecimalFormat("$#,##0.00;$-#,##0.00");
		this.stringPrice = a.format(price/100.0).toString();
		this.isMarket = false;
	}
	
	//GETTERS-------------//
	public long getPrice() {
		return price;
	}
	
	public boolean getIsMarket(){
		return isMarket;
	}
	
	public String getString(){
		return stringPrice;
	}
	
	//UTILITIES ----------//
	public String toString(){
		return getString();
	}
	
	//MATH---------------------------//
	public Price add (Price p) throws InvalidPriceException {
		if (this.getIsMarket() || p.getIsMarket() || p == null) throw new InvalidPriceException("Cannot add a LIMIT Price to a MARKET Price");
		return PriceFactory.makeLimitPrice(this.getPrice() + p.getPrice());
	}
	
	public Price subtract(Price p) throws InvalidPriceException {
		if (this.getIsMarket() || p.getIsMarket() || p == null) throw new InvalidPriceException("Cannot subtrace using a MARKET Price");
		return PriceFactory.makeLimitPrice(this.getPrice() - p.getPrice());
	}
	
	public Price multiply (int x) throws InvalidPriceException {
		if (this.getIsMarket()) throw new InvalidPriceException("Cannont multiply a MARKET Price");
		return PriceFactory.makeLimitPrice(this.getPrice() * x);
	}
	
	//COMPARISONS-------------------//
	public int compareTo(Price p){
		if (this.getPrice() < p.getPrice()) return -1;
		else if (this.getPrice() > p.getPrice()) return 1;
		else return 0;
	}
	
	public boolean greaterOrEqual(Price p) {
		return (p.getIsMarket() == false && (this.getPrice() >= p.getPrice()));
	}
	
	public boolean greaterThan(Price p) {
		return (p.getIsMarket() == false && (this.getPrice() > p.getPrice())); 
	}
	
	public boolean lessOrEqual(Price p)  {
		return (getIsMarket() == false && (this.getPrice() <= p.getPrice()));
	}
	
	public boolean lessThan (Price p)  {
		return (getIsMarket() == false && (this.getPrice() < p.getPrice()));
	}
	
	public boolean equals (Price p) {
		return (!getIsMarket() && this.getPrice() == p.getPrice());
	}
	
	public boolean isMarket() throws InvalidPriceException {
		return getIsMarket(); 
	}
	
	public boolean isNegative() throws InvalidPriceException {
		return (!getIsMarket()) && (this.getPrice() < 0);
	}
	
	public static ArrayList<Price> sort(ArrayList<Price> alist){
		for (int i = 1; i < alist.size(); i++){
			Price temp = alist.get(i);
			int j;
			for (j = i -1; j >= 0 && temp.compareTo(alist.get(j)) == 1 ; j--)
				alist.set(j+1, alist.get(j));
			alist.set(j+1, temp);
		}
		return alist;
	}
}
