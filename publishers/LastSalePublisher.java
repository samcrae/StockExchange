package publishers;

import java.util.ArrayList;

import price.Price;
import price.PriceFactory;
import client.User;


public class LastSalePublisher extends Publisher {

	TickerPublisher t = TickerPublisher.getInstance();
	
	
	//SINGLETON CONSTRUCTION--------------------//
	private static LastSalePublisher instance = new LastSalePublisher();
	public static  LastSalePublisher getInstance(){
		return instance;
	}

	
	//PUBLISH METHOD----------------// Subscribe and Unsubscribe inherited from Publisher Class
	
	/******************************
	 * @param product of last sale
	 * @param price of last sale
	 * @param volume of last sale
	 ******************************/
	public synchronized void publishLastSale(String product, Price price, int volume){
				
		if (!getSubscriptionMap().containsKey(product)) return;
		ArrayList<User> subUsers = getSubscriptionMap().get(product);
		
		if (price == null) price = PriceFactory.newPrice(0);
		for (User subscribed : subUsers){
			subscribed.acceptLastSale(product, price, volume);
		}
		t.publishTicker(product,price);
	}
}
