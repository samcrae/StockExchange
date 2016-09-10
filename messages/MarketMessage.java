package messages;

import exceptions.InvalidMessageException;

/*************************************************************
 * @author Steven Mcrae, Briant Belcote, Tom Meier 
 * Encapsulates the "state" of the market.
 *************************************************************/
public class MarketMessage {

	private MarketState state;

	public enum MarketState {
		CLOSED, PREOPEN, OPEN
	}

	public MarketMessage(MarketState s) throws InvalidMessageException  {
		setState(s);
	}

	public MarketState getState() {
		return state;
	}

	private void setState(MarketState state) throws InvalidMessageException{
		 if(state != MarketState.CLOSED && state != MarketState.PREOPEN && state != MarketState.OPEN) 
			 throw new InvalidMessageException("Invalid Market State");
		this.state = state;
	}
	
	public String toString() {
		return "" + getState();
	}
}
