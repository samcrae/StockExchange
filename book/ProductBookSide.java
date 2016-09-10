package book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import messages.CancelMessage;
import messages.FillMessage;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.OrderNotFoundException;
import price.Price;
import price.PriceFactory;
import publishers.MessagePublisher;
import tradable.Tradable;
import tradable.TradableDTO;
import tradeProcessor.TradeProcessor;
import tradeProcessor.TradeProcessorFactory;


/***************************************************************************
 * @author Steven Mcrae, Briant Belcote, Tom Meier
 *
 *ProductBookSide owns a list of the â€œbookedâ€� Tradable objects, organized by 
 *Price and by time of arrival.  An individual ProductBookSide represents 
 *the Buy side or the Sell side of the book.
 ***************************************************************************/
public class ProductBookSide implements Runnable {

	// collection of book entries for one side of the ProductBook
	private HashMap<Price, ArrayList<Tradable>> bookEntriesMap = new HashMap<Price, ArrayList<Tradable>>();
	private String side;
	private ProductBook productBook;
	//removed TradeProcessor variable - local variable used instead

	//CONSTRUCTOR --------//
	public ProductBookSide(ProductBook price, String side)  {
		if(price == null) throw new NullPointerException();
		productBook = price;
		setSide(side);
	}
	
	//////////////2.1//////////////////
	/********************************************************
	 * @param userName - user with remaining volume         
	 * @return - ArrayList of TradableDTOs  for all orders
	 * which have remaining quantity for a specified user
	 ********************************************************/
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName) {
		ArrayList<TradableDTO> remainingQtyList = new ArrayList<TradableDTO>();


		for (Entry<Price, ArrayList<Tradable>> entry : bookEntriesMap.entrySet()) {
			ArrayList<Tradable> list = entry.getValue();
			for (Tradable tradable : list) {
				if (tradable.getUser() == userName) {
					if (tradable.getRemainingVolume() > 0) {
						TradableDTO dto = new TradableDTO(tradable);
						remainingQtyList.add(dto);
					}
				}
			}
		}
		return remainingQtyList;
	}


	//////////////2.2//////////////////
	/***************************************************************************************
	 * @return -  ArrayList of the Tradables that are at the best price in the â€œbookEntriesâ€� 
	 ***************************************************************************************/
	public synchronized ArrayList<Tradable> getEntriesAtTopOfBook() {
		if (bookEntriesMap.isEmpty())return null;

		ArrayList<Price> prices = new ArrayList<Price>(bookEntriesMap.keySet()); 									
		Collections.sort(prices); 
		if (side == "BUY") {
			Collections.reverse(prices); // Reverse them
		}
		return bookEntriesMap.get(prices.get(0));
	}

	//////////////2.3//////////////////
	/*********************************************************************************
	 * @return - an array of Strings, where each index holds a Price x Volume String
	 *********************************************************************************/
	public synchronized String[] getBookDepth() {
		if (bookEntriesMap.isEmpty())
			return new String[] { "<Empty>" };
		else {
			String[] bookDepth = new String[bookEntriesMap.size()];

			ArrayList<Price> priceList = new ArrayList<Price>(bookEntriesMap.keySet()); // Get prices

			Collections.sort(priceList);
			String s = "";
			int counter = 0;
			for (Price p : priceList) {
				ArrayList<Tradable> tradableList = bookEntriesMap.get(p);
				int volume = 0;

				for (Tradable t : tradableList) {
					volume += t.getRemainingVolume();
					s = t.getPrice() + " x " + volume;
				}
				bookDepth[counter] = s;
				counter++;
			}
			return bookDepth;
		}
	}



	//////////////2.4//////////////////
	/***********************************************************************
	 * @param price - Price of desired Tradable
	 * @return -  all the Tradables in this book side at the specified price
	 ***********************************************************************/
	synchronized ArrayList<Tradable> getEntriesAtPrice(Price price) {
		if(!bookEntriesMap.containsKey(price)) return null;
		return bookEntriesMap.get(price);
	}


	//////////////2.5//////////////////
	/**********************************************************************
	 * @return - does the book have a market price?
	 * @throws InvalidPriceException 
	 **********************************************************************/
	public synchronized boolean hasMarketPrice() throws InvalidPriceException {

		boolean hasMarketPrice = false;
		@SuppressWarnings("unchecked")
		ArrayList<Price> priceList = (ArrayList<Price>) bookEntriesMap.keySet();
		for(Price p : priceList) {
			if(p.isMarket()) hasMarketPrice = true;
		}
		return hasMarketPrice;
	}


	//////////////2.6//////////////////
	/**********************************************************************
	 * @return - is the ONLY Price in this productâ€™s book a Market Price?
	 **********************************************************************/
	public synchronized boolean hasOnlyMarketPrice() {
		return bookEntriesMap.size() == 1 && bookEntriesMap.containsKey("MKT");
	}


	//////////////2.7//////////////////
	/********************************************
	 * @return -  the best Price in the book side
	 ********************************************/
	public synchronized Price topOfBookPrice(){

		if(bookEntriesMap.isEmpty())
			return PriceFactory.makeLimitPrice("0");

		ArrayList<Price> sorted = new ArrayList<Price>(bookEntriesMap.keySet());
		Collections.sort(sorted);
		if (side == "BUY") {
			Collections.reverse(sorted); // Reverse them
		}
		return sorted.get(0);
	}

	//////////////2.8//////////////////
	/***********************************************************************
	 * @return -  the volume associated with the best Price in the book side
	 ***********************************************************************/
	public synchronized int topOfBookVolume() {
		if(bookEntriesMap.isEmpty()) return 0;

		int volume = 0;
		ArrayList<Tradable> bestTradable = getEntriesAtTopOfBook();
		for(Tradable t : bestTradable) {
			volume += t.getRemainingVolume();
		}
		return volume;

	}


	//////////////2.9//////////////////
	/*****************************************
	 * @return - is the ProductBookSide empty?
	 *****************************************/
	public synchronized boolean isEmpty() {
		return bookEntriesMap.isEmpty();
	}

	/*******************************
	 * UTILITY METHODS
	 ******************************/




	//////////////2.1//////////////////
	/**
	 * @throws OrderNotFoundException 
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 * 
	 */
	public synchronized void cancelAll() throws InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException {

		ArrayList<Price> priceList= new ArrayList<Price>(bookEntriesMap.keySet());
		
		for (Price p : priceList) {
			ArrayList<Tradable> tradables = bookEntriesMap.get(p);
			for (Tradable tradable : tradables) {
			//	if (tradable.isQuote()) 
				//	submitQuoteCancel(tradable.getUser());
				submitOrderCancel(tradable.getId());
			}
		}
		for (Price p : priceList)
		bookEntriesMap.remove(p);
	}


	//////////////2.2//////////////////
	/*********************************************************
	 * Search the book for a Quote from the specified user.  
	 * Once found, remove the Quote from the book.
	 * 
	 * @param user - user to search for remove Quote
	 * @return - Tradable DTO containing data from a QuoteSide
	 *********************************************************/
	public synchronized TradableDTO removeQuote(String user)  {
		
		ArrayList<Price> priceList= new ArrayList<>(bookEntriesMap.keySet());
		ArrayList<Tradable> tradables = null;
		ArrayList<Tradable> tempList = new ArrayList<>();
		TradableDTO dto = null;
		
		//Get tradable ArrayList for each price, search for correct username
		//If user found, check to see that the Tradable is a Quote
		for (Price p : priceList) {
			tradables = bookEntriesMap.get(p);
			for (Tradable tradable : tradables) {
				if (tradable.getUser().equals(user)) {
					if (tradable.isQuote()) {
						tempList.add(tradable);
						dto = new TradableDTO(tradable);
					}
				}
			}
		}
		if (!tempList.isEmpty()){
		Tradable t = tempList.get(0);  
		tradables.remove(t);
		if (tradables.isEmpty())
			bookEntriesMap.remove(t.getPrice());
		return dto;
		}
		return null;
	}


	//////////////2.3//////////////////
	/**********************************************************************
	 * Cancel the Order (if possible) that has the specified identifier
	 * 
	 * @param orderId - ID of canceled order
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 * @throws OrderNotFoundException 
	 **********************************************************************/
	public synchronized void submitOrderCancel(String orderId) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException {

		ArrayList<Price> priceList= new ArrayList<Price>(bookEntriesMap.keySet());
		ArrayList<Tradable> tradables = null;

		// Get tradable ArrayList for each price, search for correct ID
		// If user ID found, remove that Tradable and publish a CancelMessage 
		for (Price price : priceList) {
			tradables = bookEntriesMap.get(price);
			for (Tradable tradable : tradables) {
				if(orderId.contains(tradable.getId())){
					CancelMessage cm = new CancelMessage(tradable.getUser(),
							tradable.getProduct(), tradable.getPrice(),
							tradable.getRemainingVolume(), tradable.toString(),
							tradable.getSide(), tradable.getId());
					MessagePublisher.getInstance().publishCancel(cm);
					addOldEntry(tradable);
					return;
				}
			}	
		}
		//productBook.checkTooLateToCancel(orderId); //TODO verify this works
	}
	



	//////////////2.4//////////////////
	/********************************************************************
	 * Cancel the QuoteSide (if possible) that has the specified userName
	 * 
	 * @param userName - user who wants to cancel Quote
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 ********************************************************************/
	public synchronized void submitQuoteCancel(String userName) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException {
		TradableDTO dto = removeQuote(userName);
		if (dto != null) {
			CancelMessage cm = new CancelMessage(dto.getUser(),
					dto.getSymbol(), dto.getOrdPrice(),
					dto.getRemVolume(), "Quote " + getSide() + "-Side Cancelled",
					getSide(), dto.getId());
			MessagePublisher.getInstance().publishCancel(cm);
		}
		else{return;}
	}
	//////////////2.5//////////////////
	/******************************************************************************
	 * Add the Tradable passed in to the â€œparentâ€� product bookâ€™s â€œold entriesâ€� list
	 *
	 * @param t - Tradable to add to old entries list
	 * @throws InvalidVolumeException 
	 ******************************************************************************/
	public void addOldEntry(Tradable t) throws InvalidVolumeException  {
		productBook.addOldEntry(t);
	}
	//////////////2.6//////////////////
	/******************************************
	 * Add the Tradable passed in to the book 
	 * 
	 * @param trd = Tradable to add to the book
	 ******************************************/
	public synchronized void addToBook(Tradable trd) {

		ArrayList<Tradable> tList = new ArrayList<Tradable>();

		if (!bookEntriesMap.containsKey(trd.getPrice())) {
			bookEntriesMap.put(trd.getPrice(), tList);
		}
		ArrayList<Tradable> tradableList = bookEntriesMap.get(trd.getPrice());
		tradableList.add(trd);
	}
	//////////////2.7//////////////////
	/*******************************************************************************
	 * Attempt a trade the provided Tradable against entries in this ProductBookSide
	 * 
	 * @param trd
	 * @return
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	 *******************************************************************************/


	public HashMap<String, FillMessage> tryTrade(Tradable trd) throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {

		HashMap<String, FillMessage> allFills;

		if (getSide().equals("BUY")){ 
			allFills = trySellAgainstBuySideTrade(trd);
		}
		else allFills = tryBuyAgainstSellSideTrade(trd);

		for (Entry<String, FillMessage> entry : allFills.entrySet()) {
			FillMessage fm = entry.getValue();
			MessagePublisher.getInstance().publishFill(fm);
		}
		return allFills;
	}

	/******************************************************************************
	 * Try to fill the SELL side Tradable passed in against the content of the book
	 * 
	 * @param t
	 * @return
	 * @throws InvalidMessageException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidPriceException 
	 ******************************************************************************/
	public HashMap<String, FillMessage> trySellAgainstBuySideTrade(Tradable t) throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {

		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();

		TradeProcessor tProcessor;
		
		while (t.getRemainingVolume() > 0 && !isEmpty() && t.getPrice().compareTo(topOfBookPrice()) != 1 || t.getRemainingVolume() > 0 && !isEmpty()
				&& t.getPrice().isMarket()) {

			tProcessor = TradeProcessorFactory.newProcessor(productBook.getBuy());

			HashMap<String, FillMessage> tempMessages = tProcessor.doTrade(t);
			fillMsgs = mergeFills(fillMsgs, tempMessages);
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}

	/****************************************************************
	 * Merge multiple fill messages together into one consistent list
	 * 
	 * @param existing
	 * @param newOnes
	 * @return
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 ****************************************************************/
	private HashMap<String, FillMessage> mergeFills(HashMap<String, FillMessage> existing, HashMap<String, FillMessage> newOnes) throws InvalidVolumeException, InvalidMessageException {
		if(existing.isEmpty()) 
			return new HashMap<String, FillMessage>(newOnes);
		HashMap<String, FillMessage> results = new HashMap<String, FillMessage>(existing); 

		for (String key : newOnes.keySet())  {    
			if (!existing.containsKey(key)) 
				results.put(key, newOnes.get(key));   
			else {     
				FillMessage fm = results.get(key);   
				fm.setVolume(newOnes.get(key).getVolume());  // Update the fill volume 
				fm.setDetails(newOnes.get(key).getDetails()); // Update the fill details 
			}
		}	
		return results;
	}

	/**
	 * @param trd
	 * @return
	 * @throws InvalidVolumeException
	 * @throws InvalidMessageException
	 * @throws InvalidPriceException 
	 */
	public HashMap<String, FillMessage> tryBuyAgainstSellSideTrade(Tradable trd) throws InvalidVolumeException, InvalidMessageException, InvalidPriceException {
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();

		TradeProcessor tProcessor;

		while (trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().greaterOrEqual(topOfBookPrice()) || trd.getRemainingVolume() > 0 && !isEmpty()
				&& trd.getPrice().isMarket()) {


			tProcessor = TradeProcessorFactory.newProcessor(productBook.getSell());

			HashMap<String, FillMessage> tempMessages = tProcessor.doTrade(trd);
			fillMsgs = mergeFills(fillMsgs, tempMessages);
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}

	/********************************************************************
	 * Remove an key/value pair from the book (the â€œbookEntriesâ€� HashMap)
	 * if the ArrayList associated with the Price passed in is empty
	 * 
	 * @param p - Price to remove if no ArrayList of Tradables associated
	 ********************************************************************/
	public synchronized void clearIfEmpty(Price p)  {
		ArrayList<Tradable> priceList = bookEntriesMap.get(p); 
		if(priceList.isEmpty()) {
			bookEntriesMap.remove(p);
		}
	}

	/************************************************************************************
	 * Remove the Tradable passed in from the book (when it has been traded or cancelled)
	 *
	 * @param t - Tradable to be removed
	 ************************************************************************************/

	public synchronized void removeTradable(Tradable t) {

		ArrayList<Tradable> entries = bookEntriesMap.get(t.getPrice());
		if (entries == null)
			return;

		boolean result = entries.remove(t);
		if (result == false)
			return;

		if (entries.isEmpty())
			clearIfEmpty(t.getPrice());
	}
	
	public synchronized void removeTradable(String orderId) {
		ArrayList<Tradable> tempList = new ArrayList<>();
		int indexA = orderId.indexOf("$");
		int indexB = orderId.indexOf(".") + 3;
		String price = orderId.substring(indexA, indexB);
		Price tradePrice = PriceFactory.makeLimitPrice(price);
		
		ArrayList<Tradable> entries = bookEntriesMap.get(tradePrice);
		if (entries == null) return;
		for (Tradable tradables : entries){
			if(orderId.contains(tradables.getId())){
				tempList.add(tradables);
			}
		}
		if (!tempList.isEmpty()) entries.remove(tempList.get(0));
		if (entries.isEmpty()) bookEntriesMap.remove(tradePrice);
		}

	public String getSide() {
		return side;
	}

	///add Exception handling here
	public void setSide(String sideIn)  {
		side = sideIn;
	
	}

	@Override
	public void run() {		
	}
}
