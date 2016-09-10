package book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import exceptions.DataValidationException;
import exceptions.InvalidMarketStateException;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.NoSuchProductException;
import exceptions.NullObjectException;
import exceptions.OrderNotFoundException;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import messages.MarketMessage;
import messages.MarketMessage.MarketState;

/***************************************************************************************
 * The ProductService is the FaÃ§ade to the entities that make up the Products (Stocks), 
 * and the Product Books (booked tradables on the Buy and Sell side) 
 * @author Steven Mcrae, Briant Becote, Tom Meier
 ***************************************************************************************/

public class ProductService {
	//Holds all product books, organized by stock symbol
	 private HashMap<String, ProductBook> allBooksMap = new HashMap<String, ProductBook>();
	//Initiaize MarketState to CLOSED 
	 MarketState state = MarketState.CLOSED;
	 
	//SINGLETON CONSTRUCTION--------------------//
		private static ProductService instance = null;
		private ProductService(){}
		
		public synchronized static ProductService getInstance(){
			if(instance == null){
				instance = new ProductService();
			}
			return instance;
		}
		
		
		////////4.1///////
	/**************************************************************************
	 * Returns a List of TradableDTOs containing any orders with remaining
	 * quantity for the user and the stock specified
	 * 
	 * @param userName
	 * @param product
	 * @return
	 **************************************************************************/
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(
			String userName, String product) {
		ProductBook book = allBooksMap.get(product);
		ArrayList<TradableDTO> dto = book.getOrdersWithRemainingQty(userName);
		ArrayList <TradableDTO> temp = book.getOrdersWithRemainingQty(userName);
		//Following 2 for loops remove Quotes from the getOrders results
		for (TradableDTO dtos : dto)
			if (dtos.isQuote()) temp.add(dtos);
		for (TradableDTO dtos : temp)
			dto.remove(dtos);
		return dto;
	}

		////////4.2///////
	/******************************************************************************
	 * Return a List of MarketDataDTO containing the best buy price/volume and sell 
	 * price/volume for the specified stock product
	 *
	 * @param product
	 * @return
	 ******************************************************************************/
	public synchronized MarketDataDTO getMarketData(String product) {
		ProductBook book = allBooksMap.get(product);
		//MarketDataDTO dto = book.getMarketData();
		return book.getMarketData();
		
		//return dto.product + " " + dto.buyVolume + "@"
		//+ dto.buyPrice + " x " + dto.sellVolume + "@" + dto.sellPrice;
	}
	////////4.3///////
	/************************
	 * @return - MarketState
	 ************************/
	public synchronized MarketState getMarketState() {
		return state;
	}
	
	
	
	////////4.4///////
	/**
	 * 
	 * @param product
	 * @return
	 * @throws NoSuchProductException
	 */

	public synchronized String[][] getBookDepth(String product) throws NoSuchProductException {
		if(!allBooksMap.containsKey(product))
			throw new NoSuchProductException();
		
		ProductBook book = allBooksMap.get(product);
		return book.getBookDepth();
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized ArrayList<String> getProductList() {
		return new ArrayList<String>(allBooksMap.keySet());
	}
	
	/**************************************************
	 * Market and Product Service Manipulation Methods
	 **************************************************/

	/**
	 * 
	 * @param ms
	 * @throws InvalidMarketStateException 
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws OrderNotFoundException 
	 * @throws InvalidPriceException 
	 */

	public synchronized void setMarketState(MarketState ms) throws InvalidMarketStateException, InvalidMessageException, InvalidPriceException, OrderNotFoundException, InvalidVolumeException {
		MarketState currentState = getMarketState();
		MarketState newState = ms;
		
		if(currentState == MarketState.CLOSED && newState == MarketState.OPEN){
			throw new InvalidMarketStateException("Cannot OPEN the market without a PREOPEN period");
		}
		
		if(currentState == MarketState.OPEN && newState == MarketState.PREOPEN){
			throw new InvalidMarketStateException("Cannot PREOPEN the market while it is OPEN");
		}
		
		if(currentState == MarketState.PREOPEN && newState == MarketState.CLOSED){
			throw new InvalidMarketStateException("Cannot CLOSE the market while it is in PREOPEN");
		}
		
		state = ms;
		MarketMessage msg = new MarketMessage(state);
		MessagePublisher.getInstance().publishMarketMessage(msg);

		// Get ProductBook, call open market for all
		if (newState == MarketState.OPEN) {
			for (Entry<String, ProductBook> entry : allBooksMap.entrySet()) {
				ProductBookSide sellSide = entry.getValue().getSell();
				ProductBookSide buySide = entry.getValue().getBuy();
				if (sellSide.getEntriesAtTopOfBook() == null && buySide.getEntriesAtTopOfBook() == null) System.out.println("NO opening trade in " + entry.getKey()); 
				else entry.getValue().openMarket();
			}
		}
		
			// Get ProductBook, call close market for all
		if (newState == MarketState.CLOSED) {
			for (Entry<String, ProductBook> entry : allBooksMap.entrySet())
				entry.getValue().closeMarket();
			
		}
	}

	

	/***
	 * Create a new stock product that can be used for trading. 
	 * This will result in the creation of a ProductBook object, 
	 * and a new entry in the â€œallBooksâ€� HashMap
	 * 
	 * @param product
	 * @throws DataValidationException 
	 * @throws NullObjectException 
	 */
	public synchronized void createProduct(String product) throws DataValidationException, NullObjectException {
		if(product == null || product == "") 
			throw new DataValidationException("product cannot be null");
	
		if(allBooksMap.containsKey(product))
		throw new DataValidationException();
	
		ProductBook book = new ProductBook(product);
		allBooksMap.put(product, book);
	}
	
	/**
	 * Forward the provided Quote to the appropriate product book
	 * 
	 * @param q
	 * @throws InvalidMarketStateException 
	 * @throws NoSuchProductException 
	 * @throws DataValidationException 
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	 */
	public synchronized void submitQuote(Quote q) throws InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException, InvalidVolumeException {
		if(getMarketState() == MarketState.CLOSED)
			throw new InvalidMarketStateException();
	
		if(!allBooksMap.containsKey(q.getProductSymbol()))
			throw new NoSuchProductException("product " + q.getProductSymbol() + " does not exist");
	
		ProductBook book = allBooksMap.get(q.getProductSymbol());
		book.addToBook(q);
	}
	
	/**
	 * Forward the provided Order to the appropriate product book
	 * @param o 
	 * 
	 * @param q
	 * @return 
	 * @throws InvalidMarketStateException 
	 * @throws NoSuchProductException 
	 * @throws DataValidationException 
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	 */
	public synchronized String submitOrder(Order o) throws InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		if(getMarketState() == MarketState.CLOSED)
			throw new InvalidMarketStateException("Orders are not accepted when the market is CLOSED");
	
		if(getMarketState() == MarketState.PREOPEN && o.getPrice().equals("MKT"))
			throw new InvalidMarketStateException("MKT Orders are not accepted when makret is in PREOPEN");
		
		if(!allBooksMap.containsKey(o.getProduct()))
			throw new NoSuchProductException("Product " + o.getProduct() + " does not exist");
		
		ProductBook book = allBooksMap.get(o.getProduct());
		book.addToBook(o);
		return o.toString();
	}
	
	/**
	 * Forward the provided Order Cancel to the appropriate product book
	 * 
	 * @param product
	 * @param side
	 * @param orderId
	 * @throws InvalidMarketStateException 
	 * @throws NoSuchProductException 
	 * @throws OrderNotFoundException 
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 */
	 public synchronized void submitOrderCancel(String product, String side, String orderId) throws InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException {
		 if(getMarketState() == MarketState.CLOSED) throw new InvalidMarketStateException();
	 
		 if(!allBooksMap.containsKey(product)) return;
			 		 
		 ProductBook book = allBooksMap.get(product);
		 book.cancelOrder(side, orderId);
		 allBooksMap.put(product, book);
	 }
	 
	 /**
		 * Forward the provided Quote Cancel to the appropriate product book
		 * @param userName 
		 * 
		 * @param product
		 * @param side
		 * @param orderId
		 * @throws InvalidMarketStateException 
		 * @throws NoSuchProductException 
		 * @throws OrderNotFoundException 
		 * @throws InvalidPriceException 
		 * @throws InvalidVolumeException 
		 * @throws InvalidMessageException 
		 */
		 public synchronized void  submitQuoteCancel(String userName, String product) throws InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException {
			 if(getMarketState() == MarketState.CLOSED)
					throw new InvalidMarketStateException();
		 
			 if(!allBooksMap.containsKey(product))
					throw new NoSuchProductException("Product " + product + " does not exist");	
		 
			 ProductBook book = allBooksMap.get(product);
			 book.cancelQuote(userName);//TODO TESTING
		 }
}
