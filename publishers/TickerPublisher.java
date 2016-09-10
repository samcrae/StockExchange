package publishers;

import java.util.ArrayList;
import java.util.HashMap;

import price.Price;
import price.PriceFactory;
import client.User;


public class TickerPublisher extends Publisher {

private static HashMap<String, Price> tickerCollection = new HashMap<String, Price>();

	//SINGLETON CONSTRUCTION--------------------//
	private static TickerPublisher instance = new TickerPublisher();
	public static TickerPublisher getInstance(){
		return instance;
	}

	//PUBLISH METHOD----------------// Subscribe and Unsubscribe inherited from Publisher Class
	public synchronized void publishTicker(String product, Price price){
		
		if (!getSubscriptionMap().containsKey(product)) return;
		if (price == null) price = PriceFactory.newPrice(0);
		
		Price lastPrice;
			
		if(!tickerCollection.containsKey(product)) {
			tickerCollection.put(product, price);
			lastPrice = null;
		} else {
			lastPrice = tickerCollection.get(product);
			tickerCollection.put(product, price);
		}
		
		ArrayList<User> subUsers = getSubscriptionMap().get(product);
		
		if (lastPrice == null){ 
			for (User subscribed : subUsers){
				subscribed.acceptTicker(product, price, ' ');
			}
		}
		else {
			int movement = lastPrice.compareTo(price);
			switch (movement){
			case -1: 
				for (User subscribed : subUsers){
					subscribed.acceptTicker(product, price, (char)8593);
					}
				break;
			case 0: 
				for (User subscribed : subUsers){
					subscribed.acceptTicker(product, price, '=');
					}
				break;
			case 1: 
				for (User subscribed : subUsers){
					subscribed.acceptTicker(product, price, (char)8595);
					}
				break;
			}
		}
	}
}
	