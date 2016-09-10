package publishers;

import java.util.HashSet;

import client.User;

@SuppressWarnings("serial")
public class Subscriptions extends HashSet<User> {
	private HashSet<User> subscribers; 
	
	Subscriptions(User user){
	subscribers = new HashSet<User>();
	subscribers.add(user);
	}
	
	public User get(User user){
	if (this.contains(user)) return user;
	else return null;
	}
	
	
}




