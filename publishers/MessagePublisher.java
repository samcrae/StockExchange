package publishers;

import java.util.ArrayList;

import messages.CancelMessage;
import messages.FillMessage;
import messages.MarketMessage;
import client.User;


public class MessagePublisher extends Publisher {
	
	User canceledUser;
	protected ArrayList<User> list;
	

	// SINGLETON CONSTRUCTION--------------------//
	private static MessagePublisher instance = new MessagePublisher();
	public static MessagePublisher getInstance() {
	return instance;
	}
	



	// PUBLISH METHODS----------------// Subscribe and Unsubscribe inherited from Publisher Class
	public synchronized void publishCancel(CancelMessage cm) {

		ArrayList<User> l = getSubscriptionMap().get(cm.getProduct());
		if (l == null)
			return;
		for (User u : l) {
			if (u.getUserName() == cm.getUser())
				canceledUser = u;
		}
		canceledUser.acceptMessage(cm);
	}

	public synchronized void publishFill(FillMessage fm) {
		ArrayList<User> l = getSubscriptionMap().get(fm.getProduct());
		if (l == null)
			return;
		for (User u : l) {
			if (u.getUserName() == fm.getUser())
				canceledUser = u;
		}
		canceledUser.acceptMessage(fm);
	}

	public synchronized void publishMarketMessage(MarketMessage mm) {
	
		if(getSubscriptionMap().isEmpty()) return;
		
		ArrayList<User> userList = getList();
		
		for(User u: userList) {
			u.acceptMarketMessage(mm.toString());
		}
		
	}

}
