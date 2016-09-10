package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import price.Price;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.TickerPublisher;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import book.ProductService;
import exceptions.AlreadySubscribedException;
import exceptions.ConnectionException;
import exceptions.DataValidationException;
import exceptions.InvalidMarketStateException;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.NoSuchProductException;
import exceptions.NotSubscribedException;
import exceptions.OrderNotFoundException;


public class UserCommandService {

	//hold user name and connection id pairs.
	private HashMap<String, Long> connectedUserIds = new HashMap<>();
	
	//hold user name and user object pairs
	private HashMap<String, User> connectedUsers = new HashMap<>();
	
	//hold user name and connection-time pairs, connection time is Long
	private HashMap<String, Long> connectionTimes = new HashMap<>();
	
	//SINGLETON CONSTRUCTION--------------------//
	private static UserCommandService instance = null;

	private UserCommandService() {}

	public static synchronized UserCommandService getInstance() {
		if (instance == null) {
			instance = new UserCommandService();
		}
		return instance;
	}	
		/**
		 * verify the integrity of the user name and connection id passed in
		 * 
		 * @param userName
		 * @param connId
		 */
	private void verifyUser(String userName, long connId)throws ConnectionException {
		if (!connectedUserIds.containsKey(userName)) {
			throw new ConnectionException("User Not Connected");
		}
		if(connId != connectedUserIds.get(userName)) {
			throw new ConnectionException("Invalid Connection, User Not Associated With This Connection");
		}		

	}
	
	/**
	 * Connect the user to the trading system.
	 * 
	 * @param user
	 * @return
	 * @throws ConnectionException 
	 */
	public synchronized long connect(User user) throws ConnectionException {
		if(connectedUserIds.containsKey(user)) {
			throw new ConnectionException(user + " Already Connected");
		}
		
		connectedUserIds.put(user.getUserName(), System.nanoTime());
		connectedUsers.put(user.getUserName(), user);
		connectionTimes.put(user.getUserName(), System.currentTimeMillis());
		
		return connectedUserIds.get(user.getUserName());
	}
	
	/**
	 * disconnect the user from the trading system
	 * @param userName
	 * @param connId
	 * @throws ConnectionException
	 */
	public synchronized void disConnect(String userName, long connId) throws ConnectionException {
		verifyUser(userName, connId);
		
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectionTimes.remove(userName);
	}
	
	/**
	 * @param userName
	 * @param connId
	 * @param product
	 * @return results of a call to the ProductServiceâ€™s â€œgetBookDepth(product)â€� method
	 * @throws ConnectionException
	 * @throws NoSuchProductException
	 */
	public String[][] getBookDepth(String userName, long connId, String product) throws ConnectionException, NoSuchProductException {
		verifyUser(userName, connId);
		return ProductService.getInstance().getBookDepth(product);
	}
	
	
	/**
	 * Forwards the call of â€œgetMarketStateâ€� to the ProductService.
	 * @param userName
	 * @param connId
	 * @return
	 * @throws ConnectionException
	 */
	public String getMarketState(String userName, long connId) throws ConnectionException {
		verifyUser(userName, connId);
		return ProductService.getInstance().getMarketState().toString();
	}
	
	
	/**
	 * Forwards the call of getOrdersWithRemainingQty to the ProductService.
	 * @param userName
	 * @param connId
	 * @param product
	 * @return
	 * @throws ConnectionException
	 */
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connId, String
			product) throws ConnectionException {
		verifyUser(userName, connId);
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);
	}
	
	
	
	/**
	 * @param userName
	 * @param connId
	 * @return sorted list of the available stocks on this system, received from the ProductService
	 * @throws ConnectionException
	 */
	public ArrayList<String> getProducts(String userName, long connId) throws ConnectionException {
		verifyUser(userName, connId);
		ArrayList<String> stockList = ProductService.getInstance().getProductList();
		Collections.sort(stockList);
		return stockList;
	}

	/**
	 * Create an order object using the data passed in, and forward the
	 *  order to the ProductServiceâ€™s â€œsubmitOrderâ€� method 
	 * @param userName
	 * @param connId
	 * @param product
	 * @param price
	 * @param volume
	 * @param side
	 * @return
	 * @throws ConnectionException
	 * @throws InvalidVolumeException
	 * @throws InvalidMarketStateException
	 * @throws NoSuchProductException
	 * @throws DataValidationException
	 * @throws InvalidMessageException
	 * @throws InvalidPriceException
	 */
	public String submitOrder(String userName, long connId, String product, Price price, int volume, String side) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException {
		verifyUser(userName, connId);
		Order order = new Order(userName, product, price, volume, side);
		String orderId = ProductService.getInstance().submitOrder(order);
		return orderId;
	}
	
	
	/**
	 * forward the provided information to the ProductServiceâ€™s â€œsubmitOrderCancelâ€� method.
	 * @param userName
	 * @param connId
	 * @param product
	 * @param side
	 * @param orderId
	 * @throws InvalidMarketStateException
	 * @throws NoSuchProductException
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 * @throws OrderNotFoundException
	 * @throws ConnectionException
	 */
	public void submitOrderCancel(String userName, long connId, String product, String side, String orderId) throws InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException, ConnectionException {
		verifyUser(userName, connId);
		ProductService.getInstance().submitOrderCancel(product, side, orderId);
	}
	
	
	
	/**
	 * @param userName
	 * @param connId
	 * @param product
	 * @param bPrice
	 * @param bVolume
	 * @param sPrice
	 * @param sVolume
	 * @throws ConnectionException
	 * @throws InvalidVolumeException
	 * @throws InvalidMarketStateException
	 * @throws NoSuchProductException
	 * @throws DataValidationException
	 * @throws InvalidMessageException
	 * @throws InvalidPriceException
	 */
	public void submitQuote(String userName, long connId, String product, Price bPrice, int bVolume, Price sPrice, int
			sVolume) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException {
		verifyUser(userName, connId);
		
		Quote quote = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
		ProductService.getInstance().submitQuote(quote);
	}
	
	/**
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws InvalidMarketStateException
	 * @throws NoSuchProductException
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 */
	public void submitQuoteCancel(String userName, long connId, String product) throws ConnectionException, InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException {
		verifyUser(userName, connId);
		ProductService.getInstance().submitQuoteCancel(userName, product);
	}
	
	/**
	 * forward the subscription request to the CurrentMarketPublisher.
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws AlreadySubscribedException
	 */
	public void subscribeCurrentMarket(String userName, long connId, String product) throws ConnectionException, AlreadySubscribedException {
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	/**
	 * forward the subscription request to the LastSalePublisher.
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws AlreadySubscribedException
	 */
	public void subscribeLastSale(String userName, long connId, String product) throws ConnectionException, AlreadySubscribedException {
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	/**
	 * forward the subscription request to the MessagePublisher.
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws AlreadySubscribedException
	 */
	public void subscribeMessages(String userName, long connId, String product) throws ConnectionException, AlreadySubscribedException {
		verifyUser(userName, connId);
		MessagePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	
	/**
	 * forward the subscription request to the TickerPublisher 
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws AlreadySubscribedException
	 */
	public void subscribeTicker(String userName, long connId, String product) throws ConnectionException, AlreadySubscribedException {
		verifyUser(userName, connId);
		TickerPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	/**
	 * forward the unsubscribe request to the CurrentMarketPublisher.
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws NotSubscribedException
	 */
	public void unSubscribeCurrentMarket(String userName, long connId, String product) throws ConnectionException, NotSubscribedException{
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	/**
	 * forward the unsubscribe request to the LastSalePublisher.
	 * 
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws NotSubscribedException
	 */
	public void unSubscribeLastSale(String userName, long connId, String product) throws ConnectionException, NotSubscribedException{
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	/**
	 * forward the unsubscribe request to the TickerPublisher.
	 * 
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws NotSubscribedException
	 */
	public void unSubscribeTicker(String userName, long connId, String product) throws ConnectionException, NotSubscribedException{
		verifyUser(userName, connId);
		TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	/**
	 * forward the unsubscribe request to the MessagePublisher.
	 * 
	 * @param userName
	 * @param connId
	 * @param product
	 * @throws ConnectionException
	 * @throws NotSubscribedException
	 */
	public void unSubscribeMessages(String userName, long connId, String product) throws ConnectionException, NotSubscribedException{
		verifyUser(userName, connId);
		MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	
}
