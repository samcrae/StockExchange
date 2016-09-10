package publishers;

import java.util.ArrayList;

import client.User;

public class CurrentMarketPublisher extends Publisher {
	
	
	//SINGLETON CONSTRUCTION--------------------//
	private static final CurrentMarketPublisher instance = new CurrentMarketPublisher();	
	public static  CurrentMarketPublisher getInstance(){
			return instance;
		}
	
	//PUBLISH METHOD----------------// Subscribe and unsubscribe inherited from Publisher Class
	/***********************************
	 * @param md - Data Transfer Object
	 ***********************************/
	public synchronized void publishCurrentMarket(MarketDataDTO md) {
		if (!getSubscriptionMap().containsKey(md.product)) return;
		ArrayList<User> subUsers = getSubscriptionMap().get(md.product);
		for (User subscribed : subUsers) {
			subscribed.acceptCurrentMarket(md.product, md.buyPrice,
					md.buyVolume, md.sellPrice, md.sellVolume);
		}
	}
}
