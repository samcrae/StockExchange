package messages;

import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;

import price.Price;

/*********************************************************************
 * @author Steven Mcrae, Briant Belcote, Tom Meier 
 *  Abstract class to define common data of CancelMessage and FillMessage 
 *********************************************************************/
public class Message  {
	private String user; 
	private String product; 						
	private Price price; 	
	private int volume; 
	private String details; 	
	private String side;
	public String id;

	/********************************************
	 * @param user user name of the user 					 
	 * @param product The string stock symbol 
	 * @param price The price specified 
	 * @param volume The quantity of the order 
	 * @param details A text description 
	 * @param side The side (BUY/SELL) 
	 * @param id The String identifier 
	 * @throws InvalidMessageException
	 * @throws InvalidVolumeException
	 * @throws InvalidPriceException
	 *********************************************/
	public Message(String user, String product, Price price, int volume,
			String details, String side, String id) throws InvalidMessageException, InvalidVolumeException, InvalidPriceException {
		
		setUser(user);
		setProduct(product);
		setPrice(price);
		setVolume(volume);
		setDetails(details);
		setSide(side);
		setId(id);
	}
	
	//GETTERS -----------------//
	public String getUser() {
		return user;
	}
	
	public String getProduct() {
		return product;
	}
	
	public Price getPrice() {
		return price;
	}
	
	public int getVolume() {
		return volume;
	}

	public String getDetails() {
		return details;
	}
	
	public String getSide() {
		return side;
	}
	
	public String getId() {
		return id;
	}
	
	//SETTERS -------------------//
	private void setUser(String user) throws InvalidMessageException{
		if(user == null || user == "") throw new InvalidMessageException("User cannot be null or empty");
		this.user = user;
	}

	private void setProduct(String product) throws InvalidMessageException  {
		if(product == null || product == "") throw new InvalidMessageException("Product cannot be null or empty");
		this.product = product;
	}

	private void setPrice(Price price) throws InvalidPriceException {
		if (price == null) throw new InvalidPriceException ("Invalid Input - Price is null");
		this.price = price;
	}

	public void setVolume(int volume) throws InvalidVolumeException {
		if (volume < 0) throw new InvalidVolumeException("Volume must be zero or greater");
		this.volume = volume;
	}

	public void setDetails(String details) throws InvalidMessageException  {
		if(user == null) throw new InvalidMessageException("Details cannot be null");
		this.details = details;
	}

	private void setSide(String side)  throws InvalidMessageException  {
		if(side != "BUY" && side != "SELL") throw new InvalidMessageException("Side must be BUY or SELL");
		this.side = side;
	}

	private void setId(String id) throws InvalidMessageException {
		if(user == null) throw new InvalidMessageException("ID cannot be null");
		this.id = id;
	}
}
