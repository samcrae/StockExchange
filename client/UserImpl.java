package client;

import exceptions.AlreadySubscribedException;
import exceptions.ConnectionException;
import exceptions.DataValidationException;
import exceptions.InvalidMarketStateException;
import exceptions.InvalidMessageException;
import exceptions.InvalidPriceException;
import exceptions.InvalidVolumeException;
import exceptions.NoSuchProductException;
import exceptions.OrderNotFoundException;
import gui.UserDisplayManager;
import java.util.ArrayList;
import java.sql.Timestamp;
import messages.CancelMessage;
import messages.FillMessage;
import price.Price;
import tradable.TradableDTO;

public class UserImpl implements User {

	private String userName;
	private long connectionId;
	private Position position;//holds values of users stocks, etc
	private UserDisplayManager userDisplayManager;
	
	//availableStocks filled once once connected to trading system
	private ArrayList<String>  availableStocks;
	
	//contains information on the orders this user has submitted (needed for cancelling)
	private ArrayList<TradableUserData>  userOrders = new ArrayList<>();
	
	//CONSTRUCTOR----------//
	public UserImpl(String userNameIn) throws DataValidationException {
		setUserName(userNameIn);
		position = new Position();
	}

	public String getUserName() {
		return userName;
	}

	private void setUserName(String userName) throws DataValidationException {
		if(userName.equals(null) || userName.equals("")) throw new DataValidationException("User name cannot be empty or null");
		this.userName = userName;
	}
		
	public long getConnectionId() {
		return connectionId;
	}

	public void acceptLastSale(String product, Price price, int volume) {
		try{
			if (userDisplayManager != null){
		userDisplayManager.updateLastSale(product, price, volume);
		position.updateLastSale(product, price);}}
		catch(Exception e){ e.getMessage(); e.printStackTrace();}
	}
	
	public void acceptMessage(FillMessage fm) {
		try{
			if (userDisplayManager != null){
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		String summary = "{" + timeStamp.toString()+"} Fill Message: "  + fm.toString();
		userDisplayManager.updateMarketActivity(summary);
		position.updatePosition(fm.getProduct(), fm.getPrice(), fm.getSide(), fm.getVolume());
			}}  catch(Exception e) {e.getMessage(); e.printStackTrace();}
		System.out.println();
	}

	public void acceptMessage(CancelMessage cm) {		
		try{
			if (userDisplayManager != null){
			Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
			String summary = "{" + timeStamp.toString() + "} Cancel Message: " + cm.toString();
			userDisplayManager.updateMarketActivity(summary);
			}} catch(Exception e) { e.printStackTrace(); e.getMessage(); }
		System.out.println();
	}

	public void acceptMarketMessage(String message) {
		try{
			if (userDisplayManager != null){
		userDisplayManager.updateMarketState(message);}}
		catch(Exception e) {e.printStackTrace(); e.getMessage();}
	}

	public void acceptTicker(String product, Price price, char direction) {
		try{
			if (userDisplayManager != null){
		userDisplayManager.updateTicker(product, price, direction);
			}} catch(Exception e) { e.printStackTrace(); e.getMessage(); }
	}

	public void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv) {
		try {
			if (userDisplayManager != null){
			userDisplayManager.updateMarketData(product, bp, bv, sp, sv);
			}} catch (Exception e) { e.printStackTrace(); e.getMessage(); }
	}

	public void connect() throws ConnectionException {
		connectionId = UserCommandService.getInstance().connect(this);
		availableStocks = UserCommandService.getInstance().getProducts(getUserName(), getConnectionId());
	}

	public void disConnect() throws ConnectionException {
		UserCommandService.getInstance().disConnect(getUserName(), getConnectionId());
	}
	
	public void showMarketDisplay() throws Exception {
		if(availableStocks == null) {//user not connected, don't activate display
			throw new ConnectionException("User Not Connected");
		}
		if(userDisplayManager == null) {//first time display opened
			userDisplayManager = new UserDisplayManager(this);
		}
		userDisplayManager.showMarketDisplay();
	}


	public String submitOrder(String product, Price price, int volume, String side) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException {
		String id = UserCommandService.getInstance().submitOrder(getUserName(), getConnectionId(), product, price, volume, side);
		TradableUserData tradable = new TradableUserData(getUserName(), product, side, id);
		userOrders.add(tradable);
		return id;
	}



	public void submitOrderCancel(String product, String side, String orderId) throws InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException, OrderNotFoundException, ConnectionException {
		UserCommandService.getInstance().submitOrderCancel(getUserName(), getConnectionId(), product, side, orderId);
	}


	public void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws ConnectionException, InvalidVolumeException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidMessageException, InvalidPriceException {
		UserCommandService.getInstance().submitQuote(getUserName(), getConnectionId(), product, buyPrice, buyVolume, sellPrice, sellVolume);
	}


	public void submitQuoteCancel(String product) throws ConnectionException, InvalidMarketStateException, NoSuchProductException, InvalidMessageException, InvalidVolumeException, InvalidPriceException {
		UserCommandService.getInstance().submitQuoteCancel(getUserName(), getConnectionId(), product);
	}

	public void subscribeCurrentMarket(String product) throws ConnectionException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeCurrentMarket(getUserName(), getConnectionId(), product);
	}


	public void subscribeLastSale(String product) throws ConnectionException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeLastSale(getUserName(), getConnectionId(), product);
	}


	public void subscribeMessages(String product) throws ConnectionException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeMessages(getUserName(), getConnectionId(), product);
	}

	public void subscribeTicker(String product) throws ConnectionException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeTicker(getUserName(), getConnectionId(), product);
	}


	public Price getAllStockValue() throws InvalidPriceException {
		return position.getAllStockValue();
	}

	public Price getAccountCosts() {
		return position.getAccountCosts();
	}

	public Price getNetAccountValue() throws InvalidPriceException {
		return position.getNetAccountValue();
	}


	public String[][] getBookDepth(String product) throws ConnectionException, NoSuchProductException {
		return UserCommandService.getInstance().getBookDepth(getUserName(), getConnectionId(), product);
	}

	public String getMarketState() throws ConnectionException {
		return UserCommandService.getInstance().getMarketState(getUserName(), getConnectionId());
	}


	public ArrayList<TradableUserData> getOrderIds() {
		return userOrders;
	}

	public ArrayList<String> getProductList() {
		return availableStocks;
	}


	public Price getStockPositionValue(String sym) throws InvalidPriceException {
		return position.getStockPositionValue(sym);
	}

	public int getStockPositionVolume(String product) {
		return position.getStockPositionVolume(product);
	}


	public ArrayList<String> getHoldings() {
		return position.getHoldings();
	}

	public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws ConnectionException {
		return UserCommandService.getInstance().getOrdersWithRemainingQty(getUserName(), getConnectionId(), product);
	}
	
	
	
}
