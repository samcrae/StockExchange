package tradable;
import price.*;

/**
 * Data Transfer Object design patter used to obtain all data values associated with a tradable object.
*<p>
*@author Team "Write Once - Run Everywhere": Steven McRae, Thomas Meier, Briant Becote 
*@version 1.1 April 23 2015
*/
public class TradableDTO {
	private String user;
	private String symbol; 
	private Price ordPrice; 
	private int origVolume; 
	private String buyOrSell;
	private String id;
	private int remVolume;
	private int canVolume;
	private boolean isQuote;
	
	//CONSTRUCTORS-----------------------//
	public TradableDTO(String product, Price price, int originalVolume, int remainingVolume, int cancelledVolume, String user, String side, boolean quote, String id) {		
		this.symbol = product;
		this.ordPrice = price; 
		this.origVolume = originalVolume;
		this.remVolume = remainingVolume;
		this.canVolume = cancelledVolume;
		this.user = user;
		this.buyOrSell = side;
		this.isQuote = quote;
		this.id = id;
	}
	
	public TradableDTO(Tradable q){
		this.symbol = q.getProduct();
		this.ordPrice = q.getPrice();
		this.origVolume = q.getOriginalVolume();
		this.remVolume = q.getRemainingVolume();
		this.canVolume = q.getCancelledVolume();
		this.user = q.getUser();
		this.buyOrSell = q.getSide();
		this.isQuote = q.isQuote();
		this.id = q.getId();
	}
	
	//UTILITY -------------------//
	public String toString(){
		return "Product: " + symbol + ", Price: " + ordPrice + ", OriginalVolume: " + origVolume + ", RemainingVolume: " + remVolume + ", CancelledVolume: "
				+ canVolume + ", User: " +user +", Side: " + buyOrSell + ", IsQuote: " + isQuote + ", Id: " + id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Price getOrdPrice() {
		return ordPrice;
	}

	public void setOrdPrice(Price ordPrice) {
		this.ordPrice = ordPrice;
	}

	public int getOrigVolume() {
		return origVolume;
	}

	public void setOrigVolume(int origVolume) {
		this.origVolume = origVolume;
	}

	public String getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(String buyOrSell) {
		this.buyOrSell = buyOrSell;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRemVolume() {
		return remVolume;
	}

	public void setRemVolume(int remVolume) {
		this.remVolume = remVolume;
	}

	public int getCanVolume() {
		return canVolume;
	}

	public void setCanVolume(int canVolume) {
		this.canVolume = canVolume;
	}

	public boolean isQuote() {
		return isQuote;
	}

	public void setQuote(boolean isQuote) {
		this.isQuote = isQuote;
	}
}
