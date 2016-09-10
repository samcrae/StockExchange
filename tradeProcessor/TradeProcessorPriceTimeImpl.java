package tradeProcessor;

import java.util.ArrayList;
import java.util.HashMap;

import book.ProductBookSide;
import price.Price;
import tradable.Tradable;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import messages.FillMessage;

public class TradeProcessorPriceTimeImpl implements TradeProcessor {
	private HashMap<String, FillMessage> fillMessages; 
	private ProductBookSide bookSide;

	//CONSTRUCTOR------//
	//visible to support Flyweight pattern with TradeProcessorFactory
	TradeProcessorPriceTimeImpl(ProductBookSide bookside) {
		setBookSide(bookside);
		fillMessages = new HashMap<String, FillMessage>();
	}

	//GETTERS AND SETTERS-----//
	public ProductBookSide getBookSide() {
		return bookSide;
	}

	private void setBookSide(ProductBookSide bookSide)  {
		this.bookSide = bookSide;
	}

	/********************************************************************
	 * All trades result in Fill Messages. All Fill Messages need a Trade 
	 * Key value so the system can tell them apart 
	 * 
	 * @param fm - FillMessage used to generate String key
	 * @return - Trade key used to identify message
	 ********************************************************************/
	private String makeFillKey(FillMessage fm)  {
		return fm.getUser() + fm.getId() + fm.getPrice();
	}

	/**
	 * Checks the content of the â€œfillMessagesâ€� HashMap to see if the FillMessage passed in is a fill message for an existing known trade 
	 * or if it is for a new previously unrecorded trade
	 * 
	 * @param fm
	 * @return
	 */
	private boolean isNewFill(FillMessage fm) {
		String key = makeFillKey(fm);

		if(!fillMessages.containsKey(key)) return true;//New trade

		FillMessage oldFill = fillMessages.get(key);

		if(!oldFill.getSide().equals(fm.getSide())) return true; //New trade

		if(!oldFill.getId().equals(fm.getId())) return true; //New trade

		return false;
	}

	/**
	 * Add a FillMessage either to the â€œfillMessagesâ€� HashMap if it is a new trade, or should update an 
	 * existing fill message is another part of an existing trade
	 * 
	 * @param fm
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 */
	private void addFillMessage(FillMessage fm) throws InvalidVolumeException, InvalidMessageException {

		if (isNewFill(fm)) {
			String key = makeFillKey(fm);
			fillMessages.put(key, fm);
		} 
		else {// not a new trade
			String key = makeFillKey(fm);
			FillMessage message = fillMessages.get(key);
			message.setVolume(message.getVolume() + fm.getVolume());
			message.setDetails(fm.getDetails());
		}
	}


	//START HERE/////////////////////
	/**
	 * Called when it has been determined that a Tradable (i.e., a Buy Order, a Sell QuoteSide, etc.) 
	 * can trade against the content of the boo
	 * @throws InvalidPriceException 
	 * @throws InvalidVolumeException 
	 * @throws InvalidMessageException 
	 */
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidPriceException, InvalidMessageException, InvalidVolumeException {

		fillMessages = new HashMap<String, FillMessage>(); 

		ArrayList<Tradable> tradeOut =  new ArrayList<Tradable>(); 
		ArrayList<Tradable> entriesAtPrice  = bookSide.getEntriesAtTopOfBook(); 

		Price tPrice;

		for(Tradable t : entriesAtPrice){

			if(trd.getRemainingVolume() == 0) {//go to after for section, yes route
				break;
			}
			if (trd.getRemainingVolume() >= t.getRemainingVolume()) {
				tradeOut.add(t);
				if (t.getPrice().isMarket()) {
					tPrice = trd.getPrice();
				} else {
					tPrice = t.getPrice();
				}
				FillMessage fm = new FillMessage(t.getUser(), t.getProduct(), tPrice, t.getRemainingVolume(),
						"leaving 0", t.getSide(), t.getId());
				addFillMessage(fm);
				fm = new FillMessage(trd.getUser(), trd.getProduct(), tPrice, t.getRemainingVolume(),
						"leaving " + (trd.getRemainingVolume() - t.getRemainingVolume()), trd.getSide(), trd.getId());
				addFillMessage(fm);

				trd.setRemainingVolume(trd.getRemainingVolume() - t.getRemainingVolume());
				t.setRemainingVolume(0);
				
				bookSide.addOldEntry(t);

			} 
			else {
				int remainder = t.getRemainingVolume()
						- trd.getRemainingVolume();

				if (t.getPrice().isMarket()) tPrice = trd.getPrice();
				else tPrice = t.getPrice();

				FillMessage fm = new FillMessage(t.getUser(), t.getProduct(), tPrice, trd.getRemainingVolume(),
						"leaving " + remainder, t.getSide(), t.getId());
				addFillMessage(fm);

				fm = new FillMessage(trd.getUser(), trd.getProduct(), tPrice, trd.getRemainingVolume(),
						"leaving 0" , trd.getSide(), trd.getId());
				addFillMessage(fm);

				trd.setRemainingVolume(0);
				t.setRemainingVolume(remainder);
				
				bookSide.addOldEntry(trd);//go to after for section, no route
			}
		}

		for(Tradable tradable : tradeOut) {
			entriesAtPrice.remove(tradable);
		}
			if(entriesAtPrice.isEmpty()) 
				bookSide.clearIfEmpty(bookSide.topOfBookPrice());
		
		return fillMessages;
	}
}
