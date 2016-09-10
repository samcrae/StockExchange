package client;

import exceptions.DataValidationException;

public class TradableUserData {
	
	private String userName;
	private String side;
	private String orderId;
	private String product;
	
	public TradableUserData (String userNameIn, String sideIn, String orderIdIn, String stockSymbolIn) throws DataValidationException  {
		setUserName(userNameIn);
		setSide(sideIn);
		setOrderId(orderIdIn);
		setStockSymbol(stockSymbolIn);
	}
	
	
	public String getUserName() {
		return userName;
	}
	public String getSide() {
		return side;
	}
	public String getOrderId() {
		return orderId;
	}
	public String getProduct() {
		return product;
	}
	
	
	private void setUserName(String userName) throws DataValidationException {
		if(userName == ""  || userName == null  ) throw new DataValidationException("Username cannot be empty or null");
		this.userName = userName;
	}
	private void setSide(String side) throws DataValidationException {
		if(side == ""  || side == null  ) throw new DataValidationException("Side cannot be empty or null");
		this.side = side;
	}
	private void setOrderId(String orderId)throws DataValidationException {
		if(orderId == ""  || orderId == null  ) throw new DataValidationException("Order ID cannot be empty or null");
		this.orderId = orderId;
	}
	private void setStockSymbol(String stockSymbol) throws DataValidationException {
		if(stockSymbol == ""  || stockSymbol== null  ) throw new DataValidationException("Stock cannot be empty or null");
		this.product = stockSymbol;
	}

}
