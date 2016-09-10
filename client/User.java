package client;

import java.util.ArrayList;

import exceptions.AlreadySubscribedException;
import exceptions.ConnectionException;
import exceptions.DataValidationException;
import exceptions.InvalidMarketStateException;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.NoSuchProductException;
import exceptions.OrderNotFoundException;
import messages.CancelMessage;
import messages.FillMessage;
import price.Price;
import tradable.TradableDTO;

public interface User {

	String getUserName(); 
	// return the String username of this user 
	
	void acceptLastSale(String product, Price p, int v); 
	//This will accept a String stock symbol
	
	void acceptMessage(FillMessage fm); 
	//This will accept a FillMessage object which contains information related to an order or quote trade
	
	void acceptMessage(CancelMessage cm);
	//This will accept a CancelMessage object which contains information related to an order or quote cancel
	
	void acceptMarketMessage(String message);
	//This will accept a String which contains market information related to a Stock Symbol they are interested in
	
	void acceptTicker(String product, Price p, char direction);
	//This will accept a stock symbol, a Price object holding the value of the last sale (trade) of that stock, 
	//and a �char� indicator of whether the �ticker� price represents an increase or decrease in the Stock�s price
	
	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv);
	//This will accept a String stock symbol, a Price object holding the current BUY side price for that stock, 
	//an int holding the current BUY side volume (quantity),  a Price object holding the current SELL side price for that stock, 
	//and an int holding the current SELL side volume (quantity). These values as a group tell the user the �current market� for a stock. For example:    AMZN:  BUY 220@12.80 and SELL 100@12.85. This info is used by 
	//�Users� to update their market display screen so that they are always looking at the most current market data.  
	
	void connect() throws ConnectionException; //Instructs a User object to connect to the trading system.
	
	void disConnect() throws ConnectionException; //Instructs a User object to disconnect from the trading system.
	
	void showMarketDisplay() throws ConnectionException, Exception; //Requests the opening of the market display if the user is connected.
	
	String submitOrder(String product, Price price, int volume, String  side) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException; //Allows the User object to submit a new
	//Order request
	
	void submitOrderCancel(String product, String side, String orderId) throws InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException, ConnectionException; //Allows the User object to submit a new Order
	//Cancel request
	
	void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException; //Allows the User
	//object to submit a new Quote request
	
	void submitQuoteCancel(String product) throws ConnectionException, InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException; //Allows the User object to submit a new Quote Cancel request
	
	void subscribeCurrentMarket(String product) throws ConnectionException, AlreadySubscribedException; //Allows the User object to subscribe for Current Market for the specified
	//Stock.
	
	void subscribeLastSale(String product) throws ConnectionException, AlreadySubscribedException; //Allows the User object to subscribe for Last Sale for the specified Stock.
	
	void subscribeMessages(String product) throws ConnectionException, AlreadySubscribedException; //Allows the User object to subscribe for Messages for the specified Stock.
	
	void subscribeTicker(String product) throws ConnectionException, AlreadySubscribedException; //Allows the User object to subscribe for Ticker for the specified Stock.
	
	Price getAllStockValue() throws InvalidPriceException; //Returns the value of the all Sock the User owns (has bought but not sold)
	
	Price getAccountCosts(); //Returns the difference between cost of all stock purchases and stock sales
	
	Price getNetAccountValue() throws InvalidPriceException; //Returns the difference between current value of all stocks owned and the account costs
	
	String[][] getBookDepth(String product) throws ConnectionException, NoSuchProductException; //Allows the User object to submit a Book Depth request for the specified stock.
	
	String getMarketState() throws ConnectionException; //Allows the User object to query the market state (OPEN, PREOPEN, CLOSED).
	
	ArrayList<TradableUserData> getOrderIds(); // Returns a list of order id’s for the orders this user has submitted.
	
	ArrayList<String> getProductList(); //Returns a list of the stock products available in the trading system.
	
	Price getStockPositionValue(String sym) throws InvalidPriceException; //Returns the value of the specified stock that this user owns
	
	int getStockPositionVolume(String product); //Returns the volume of the specified stock that this user owns
	
	ArrayList<String> getHoldings(); //Returns a list of all the Stocks the user owns
	
	ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws ConnectionException; //Gets a list of DTO’s containing information on
	//all Orders for this user for the specified product with remaining volume.
}
