package book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import messages.CancelMessage;
import messages.FillMessage;
import messages.MarketMessage.MarketState;
import exceptions.DataValidationException;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.NullObjectException;
import exceptions.OrderNotFoundException;
import price.Price;
import price.PriceFactory;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradable.Order;
import tradable.Quote;
import tradable.QuoteSide;
import tradable.Tradable;
import tradable.TradableDTO;


/***************************************************************************
 * @author Steven Mcrae, Briant Belcote, Tom Meier
 *
 *A ProductBook object maintains the Buy and Sell sides of a stock.
 *Many of the functions owned by the ProductBook class are designed to simply 
 *pass along that same call to either the Buy or Sell side of the book
 ***************************************************************************/
public class ProductBook {

	private String symbol;
	private ProductBookSide buy; 
	private ProductBookSide sell; 
	private String lastCurrentMarket = "";
	private HashSet<String> userQuotesSet = new HashSet<String>();
	private HashMap<Price, ArrayList<Tradable>> oldEntriesMap = new HashMap<Price, ArrayList<Tradable>>(); 
	
	//CONSTRUCTOR-----------//
	public ProductBook(String s) throws NullObjectException {
		setSymbol(s);
		buy = new ProductBookSide(this, "BUY");
		sell = new ProductBookSide(this, "SELL");
	}
	
	//GETTERS ------//
	public ProductBookSide getBuy() {
		return buy;
	}

	public ProductBookSide getSell() {
		return sell;
	}

	/////////3.1///////////////
	 /*********************************************** 
	  * @param userName - user with active orders
	  * @return -  ArrayList<TradableDTO> objects 
	  * that contain the details on all orders from 
	  * the specified user that are still active with 
	  * remaining volume from both sides of the book 
	  * (Buy/Sell)
	  ***********************************************/
	 public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName) {
		 ArrayList<TradableDTO> dtoList = new ArrayList<TradableDTO>();
		 
		 ArrayList<TradableDTO> buyList = buy.getOrdersWithRemainingQty(userName);
		 dtoList.addAll(buyList);
		 
		 ArrayList<TradableDTO> sellList = sell.getOrdersWithRemainingQty(userName);
		 dtoList.addAll(sellList);
		 
		 return dtoList;
	 }
	
		 
	 //////////3.2/////////
	/*****************************************************************************************************
	 * Determine if it is too late to cancel an order (meaning it has already been traded out or cancelled
	 * 
	 * @param orderId
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 * @throws OrderNotFoundException 
	 *****************************************************************************************************/
	public void checkTooLateToCancel(String orderId) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException {
		ArrayList<Tradable> tradable;
		for(Entry<Price, ArrayList<Tradable>> entry : oldEntriesMap.entrySet()){
			tradable = entry.getValue();
			for(Tradable t : tradable) {
				if(t.getId() == orderId) {
					CancelMessage cm = new CancelMessage(t.getUser(),
							t.getProduct(), t.getPrice(),
							t.getCancelledVolume(), "Too late to cancel.",
							t.getSide(), t.getId());
					MessagePublisher.getInstance().publishCancel(cm);
				}
				else {throw new OrderNotFoundException();}
			}
		}
	}
	
	/////////3.3////////////////
	/*************************************************************************
	 * @return -  2-dimensional array of Strings that contain the prices 
	 * and volumes at all prices present in the buy and sell sides of the book
	 *************************************************************************/
	 public synchronized String[ ][ ] getBookDepth() {

		 String[][] bd = new String[2][];
		 bd[0] = buy.getBookDepth();
		 Collections.reverse(Arrays.asList(bd[0]));
		 bd[1] = sell.getBookDepth();
		 
		 return bd;
	 }
	 
	 /////////3.4////////////////
	 /***********************************************************************
	  * Create a MarketDataDTO containing the best buy side price and volume, 
	  * and the best sell side price an volume
	  * 
	  * @return - MarketDataDTO containing buy/sell price/volume
	  ***********************************************************************/
	 public synchronized MarketDataDTO getMarketData() {
		Price bestBuyPrice = buy.topOfBookPrice();
		Price bestSellPrice = sell.topOfBookPrice();
		
		if(bestBuyPrice == null) 
			bestBuyPrice = PriceFactory.makeLimitPrice("0.00");
		
		if(bestSellPrice == null) 
			bestSellPrice = PriceFactory.makeLimitPrice("0.00");
		
		int bestBuyVolume = buy.topOfBookVolume();
		int bestSellVolume = sell.topOfBookVolume();
		
		MarketDataDTO dto = new MarketDataDTO(getSymbol(), bestBuyPrice, bestBuyVolume, bestSellPrice, bestSellVolume);
		
		return dto;
	 }
	 
	 /**********************
	  * MANIPULATION METHODS
	  **********************/
	 
	 
	 /////////3.2////////////////
	/********************************************************
	 * Add the Tradable passed in to the oldEntries HashMap
	 * 
	 * @param t - Tradable to add to HashMap
	 * @throws InvalidVolumeException 
	 ********************************************************/
	public void addOldEntry(Tradable t) throws InvalidVolumeException {
	
		if (!oldEntriesMap.containsKey(t.getPrice())) {
			ArrayList<Tradable> list = new ArrayList<Tradable>();
			oldEntriesMap.put(t.getPrice(), list);
		}
		t.setCancelledVolume(t.getRemainingVolume());
		t.setRemainingVolume(0);

		ArrayList<Tradable> newList = oldEntriesMap.get(t.getPrice());
		newList.add(t);

	}
	
	
	////3.3////////////
	/******************************
	 * Open the book for trading
	 * 
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	 ******************************/
	public synchronized void openMarket() throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		
		Price buyPrice = buy.topOfBookPrice();
		Price sellPrice = sell.topOfBookPrice();

		if (buyPrice.equals(PriceFactory.makeLimitPrice("$0.00")) || sellPrice.equals(PriceFactory.makeLimitPrice("$0.00"))) return;
		
		while (buyPrice.compareTo(sellPrice) != -1 
				|| sellPrice.equals("MKT") || buyPrice.equals("MKT")) {
			
			ArrayList<Tradable> topOfBuySide = buy.getEntriesAtPrice(buyPrice);
			
			HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		
			ArrayList<Tradable> toRemove = new ArrayList<Tradable>();//Tradables to be removed once opening trading completed
			
			for(Tradable t : topOfBuySide) {
				allFills = sell.tryTrade(t);
				if(t.getRemainingVolume() == 0)
					toRemove.add(t);
			}
			
			for(Tradable t : toRemove) {
				buy.removeTradable(t);//removed traded items from book
			}
		
			updateCurrentMarket();
			
			Price lastSalePrice = determineLastSalePrice(allFills);
			int lastSaleVolume = determineLastSaleQuantity(allFills);
			
			LastSalePublisher publisher = LastSalePublisher.getInstance();
			publisher.publishLastSale(getSymbol(), lastSalePrice, lastSaleVolume);
			
			buyPrice = buy.topOfBookPrice();
			sellPrice = sell.topOfBookPrice();
			
			if (buyPrice == null || sellPrice == null) break;
		}
		
	}
	
	
	///////////3.4////////////////
	/******************************
	 * Close the book for trading
	 * @throws OrderNotFoundException 
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 ******************************/
	public synchronized  void closeMarket() throws InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException {
		buy.cancelAll();
		sell.cancelAll();
		updateCurrentMarket();
	}
	
	
	
	///////////////3.5///////////////////////
	/***********************************************
	 * Cancel order on specified side
	 * 
	 * @param side - side of caneled order, BUY/SELL
	 * @param orderId - Id of order to be canceled
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 * @throws OrderNotFoundException
	 ***********************************************/
	 public synchronized void cancelOrder(String side, String orderId) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException  {
		 if(side == "BUY"){
			 buy.submitOrderCancel(orderId);
			 buy.removeTradable(orderId);
		 }
		 if(side == "SELL"){
			 sell.submitOrderCancel(orderId);
			 sell.removeTradable(orderId);
		 }
		 updateCurrentMarket();
	 }
	 
//		public Order(String userName, String productSymbol, Price orderPrice,
//				int originalVolume, String side)
	 
	 
	 
	 /////////////3.6///////////////////////
	 /**
	  *  cancel the specified user Quote on the both 
	  *  the BUY and SELL sides
	  * 
	  * @param userName
	  * @throws InvalidMessageException
	  * @throws InvalidVolumeException
	  * @throws InvalidPriceException
	  */
	 public synchronized void cancelQuote(String userName) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException {
		 buy.submitQuoteCancel(userName);
		 sell.submitQuoteCancel(userName);
		 updateCurrentMarket();
	 }
	 
	 
	 
	 ///////////3.7//////////////
	 /****************************************
	  * Add the provided Quotesides to the
	  * Buy and Sell ProductSideBooks
	  * 
	  * @param q - quote to be added to the book
	  * @throws DataValidationException
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	  ****************************************/
	 public synchronized void addToBook(Quote q) throws DataValidationException, InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		 
		 QuoteSide sellSide = q.getQuoteSide("SELL");
		 QuoteSide buySide = q.getQuoteSide("BUY");
		 
		 if (sellSide.getPrice().compareTo(buySide.getPrice()) != 1)  throw new DataValidationException();
		
		 //Get Price object of Quoteside, then long price of that Price object		 
		 long sellPrice = sellSide.getPrice().getPrice();
		 long buyPrice = buySide.getPrice().getPrice();
		 if(sellPrice <= 0 || buyPrice <= 0)  throw new DataValidationException();
		 if(sellSide.getOriginalVolume() <= 0 || buySide.getOriginalVolume() <= 0)  throw new DataValidationException();
		 
		 if (userQuotesSet.contains(q.getUserName())){
			buy.removeQuote(q.getUserName());
			
			sell.removeQuote(q.getUserName());
			updateCurrentMarket();
		 }
		 
		 addToBook("BUY", buySide);
		 addToBook("SELL", sellSide);
		 
		 userQuotesSet.add(q.getUserName());
		 updateCurrentMarket();
	 }
	 
	 
	 //////////////3.8////////////////////
	 /************************************
	  * Add the provided Order 
	  * to the appropriate ProductSideBook
	  * 
	  * @param o
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	  ************************************/
	 public synchronized void addToBook(Order o) throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		 addToBook(o.getSide(), o);
		 updateCurrentMarket();
	 }
	 
	 
	 ////////////////3.9/////////////////////
	/***************************************************
	 *Determine if the market for this stock product 
	 *has been updated by some market action
	 ***************************************************/
	public synchronized void updateCurrentMarket() {

		String s = buy.topOfBookPrice().toString() + buy.topOfBookVolume() +
				sell.topOfBookPrice().toString() + sell.topOfBookVolume();

		if(!(lastCurrentMarket.equals(s)))  {
			MarketDataDTO dto = new MarketDataDTO(getSymbol(),
					buy.topOfBookPrice(), buy.topOfBookVolume(),
					sell.topOfBookPrice(), sell.topOfBookVolume());
			CurrentMarketPublisher publisher = CurrentMarketPublisher.getInstance();
			publisher.publishCurrentMarket(dto);
			lastCurrentMarket = s;
		}		
	}
	
	
	//////////////3.10/////////////
	/******************************************
	 * Determines the last sale Price
	 * 
	 * @param fills - HashMap of fill messages
	 * @return - last sale Price
	 ******************************************/
	 private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills, String side) {
		 ArrayList<FillMessage> msgs = new ArrayList<FillMessage>(fills.values()); 
		 Collections.sort(msgs);
		 if (side.equals("BUY")) Collections.reverse(msgs);
		 return msgs.get(0).getPrice(); 
	 }
	 
	 private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills) {
		 ArrayList<FillMessage> msgs = new ArrayList<FillMessage>(fills.values()); 
		 Collections.sort(msgs);
		 return msgs.get(0).getPrice(); 
	 }
	 
	 
	 
	 ///////////3.11//////////////////////
	 /******************************************
		 * Determines the last sale quantity
		 * 
		 * @param fills - HashMap of fill messages
		 * @return - last sale quantity/volume
		 ******************************************/
		 private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills) {
			 ArrayList<FillMessage> msgs = new ArrayList<FillMessage>(fills.values()); 
			 Collections.sort(msgs); 
			 return msgs.get(0).getVolume(); 
		 }
	 
		 
		 /**********************************************************************
		  * Deals with the addition of Tradables to the Buy/Sell ProductSideBook 
		  * and handles the results of any trades the result from that addition
		  * 
		  * @param side
		  * @param trd
		  * @throws InvalidMessageException 
		  * @throws InvalidVolumeException 
		  * @throws InvalidPriceException 
		  **********************************************************************/
	
	//////////3.12//////////////
	private synchronized void addToBook(String side, Tradable trd) throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		if (ProductService.getInstance().getMarketState() == MarketState.PREOPEN) {
			if (side.equals("BUY")) 
				buy.addToBook(trd);
			else sell.addToBook(trd);
			return;
		}

		HashMap<String, FillMessage> allFills = null;
		
		if(side.equals("BUY")) 
			allFills = sell.tryTrade(trd);
		if(side.equals("SELL")) 
			allFills = buy.tryTrade(trd);		

		if (allFills != null && !allFills.isEmpty()) {
			updateCurrentMarket();
			int tradedVolume = trd.getOriginalVolume() - trd.getRemainingVolume();
			Price lastSalePrice = determineLastSalePrice(allFills, side);
			LastSalePublisher.getInstance().publishLastSale(symbol, lastSalePrice, tradedVolume);
		}
		
		if(trd.getRemainingVolume() > 0) {
			if(trd.getPrice().isMarket()){
				CancelMessage msg = new CancelMessage(trd.getUser(),
						trd.getProduct(), trd.getPrice(),
						trd.getRemainingVolume(), "Cancelled",
						trd.getSide(), trd.getId());
				MessagePublisher.getInstance().publishCancel(msg);
			} else {
				if (side.equals("BUY")) 
					buy.addToBook(trd);
			    else sell.addToBook(trd);
			}
		}
	}
	
	public String getSymbol() {
		return symbol;
	}

	private void setSymbol(String symbol) throws NullObjectException {
		if (symbol == null)
			throw new NullObjectException();
		this.symbol = symbol;
	}


	
}
