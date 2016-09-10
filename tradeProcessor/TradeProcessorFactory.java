package tradeProcessor;

import java.util.HashMap;
import book.ProductBookSide;


public class TradeProcessorFactory  {
	private static HashMap <ProductBookSide, TradeProcessor> bookMap = new HashMap<ProductBookSide, TradeProcessor>();
	
	//Factory Method-------------//
	public static TradeProcessor newProcessor(ProductBookSide bookside){
		if(bookMap.containsKey(bookside)) return bookMap.get(bookside);
		
		TradeProcessor tradeProcessor = new TradeProcessorPriceTimeImpl(bookside);
		bookMap.put(bookside, tradeProcessor);
		
		return tradeProcessor;
	}
}
