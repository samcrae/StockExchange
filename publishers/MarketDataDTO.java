package publishers;

import price.Price;

public class MarketDataDTO {

	public String product; //The stock product (i.e., IBM) that these market data elements describe
	public Price buyPrice; // The current BUY side price of the Stock
	public int buyVolume; //The current BUY side volume (quantity) of the Stock 
	public Price sellPrice; //The current SELL side price of the Stock
	public int sellVolume; //The current SELL side volume (quantity) of the Stock 

	//CONSTRUCTOR---------------//
	public MarketDataDTO(String productIn, Price buyPriceIn, int buyVolumeIn, Price sellPriceIn, int sellVolumeIn) {		
		product = productIn;
		buyPrice = buyPriceIn; 
		buyVolume = buyVolumeIn;
		sellPrice = sellPriceIn;
		sellVolume = sellVolumeIn;
	}

}
