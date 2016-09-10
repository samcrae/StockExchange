package publishers;


import java.util.ArrayList;
import java.util.HashMap;

import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

/******************************************************
 * Superclass Publishers will inherit from.  Synchronized to prevent multiple User threads from subscribing/unsubscribing at the same time
 * @author Steven Mcrae, Briant Belcote, Tom Meier 
 ******************************************************/
public class Publisher {
	

	private ArrayList<User> list;
	private HashMap <String, ArrayList<User>> subscriptionMap = new HashMap<String, ArrayList<User>>();
	
	/****************************************************************
	 * @param u - User who wishes to subscribe
	 * @param product - Stock user wants to subscribe to
	 * @throws AlreadySubscribedException - Did the user already subscribe?
	 ****************************************************************/
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException {
		
		if(!subscriptionMap.containsKey(product)) {
			list = new ArrayList<User>();
			list.add(u);
			subscriptionMap.put(product, list);
			
		}
		else{
			if(subscriptionMap.get(product).contains(u))throw new AlreadySubscribedException(); 
			ArrayList<User> l = subscriptionMap.get(product);
			l.add(u);
			subscriptionMap.put(product, l);			
			}
		}

	/****************************************************************
	 * @param u - User who wishes to unsubscribe
	 * @param product - Stock user wants to unsubscribe from
	 * @throws NotSubscribedException - Is the user not subscribed?
	 ****************************************************************/
	public synchronized void unSubscribe(User u, String product)throws NotSubscribedException {
		if(!subscriptionMap.get(product).contains(u)) throw new NotSubscribedException();
		subscriptionMap.get(product).remove(u);
	}
	
	public HashMap <String, ArrayList<User>> getSubscriptionMap() {
		return subscriptionMap;
	}
	
	public ArrayList<User> getList() {
		return list;
	}
}
